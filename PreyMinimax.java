import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 3
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */

public class PreyMinimax implements Agent{
    private static final double Ptrip = 0.2;
    private Position startPos, myPos;
    RelativeStateRep policy;
    StateRepV vValues;
    Position startPred;
    Position predPos;
    int nrRuns, maxNrRuns;
    double learningRate;
    double epsilon;
    double largestDif,init;
    
    /**
     * Constructor for the prey using Minimax-Q
     * @param startPos = position the prey starts at
     * @param startPred = position the predator starts at
     * @param init = initial value for the V-values
     * @param learningRate = learning rate for Minimax-Q algorithm
     * @param epsilon = convergence measure
     */
    public PreyMinimax(Position startPos, Position startPred, double init, double learningRate, double epsilon){
        this.startPos = startPos;
        myPos = new Position(startPos);
        this.startPred  = startPred;
        this.predPos = new Position(startPred);
        policy = new RelativeStateRep(1.0/Action.nrActionsDouble);
        vValues = new StateRepV(init, true);
        this.learningRate = learningRate;
        this.epsilon = epsilon;
        this.init = init;
    }
    
    /**
     * returns a move based on the policy of the prey
     * @param others = array list of positions with the position of the predator
     */
    @Override
    public void doMove(ArrayList<Position> others) {
        predPos = new Position(others.get(0));
        double p = Math.random();
        // if the prey does not trip
        if(p>Ptrip){
            int linIndex = policy.getLinearIndexFromPositions(myPos, predPos);
            double [] prob = policy.getStateActionPairValues(linIndex);
            double [] probCum = new double [Action.nrActions];
            // make probabilities cumulativ
            probCum[0] = prob[0];
            for(int i = 1; i<Action.nrActions-1;i++){
                probCum[i] = probCum[i-1]+prob[i];
            }
            probCum[Action.nrActions-1] = 1.0;
            // draw value, get corresponding action and return action
            p = Math.random();
            int action = -1;
            for(int i = 0; i<Action.nrActions;i++){
                if(p<=probCum[i]){
                    action = i;
                    break;
                }
            }
            myPos.adjustPosition(policy.getMove(predPos, myPos, Action.getAction(action), false));
        }
    }

    /**
     * returns position of the prey
     * @return position
     */
    @Override
    public Position getPos() {
        return myPos;
    }

    /**
     * resets the prey for the next run in the environment
     */
    @Override
    public void reset() {
        myPos = new Position(startPos);
        predPos = new Position(startPred);
    }

    /**
     * returns whether or not the agent has converged to stabel v-values
     * @return boolean that says if the agent has converged
     */
    @Override
    public boolean isConverged() {
        return largestDif<epsilon;
    }

    /**
     * receives new position of predator after a move in the environment
     * @param reward = received reward
     * @param others  = arraylist with the position of the predator
     */
    @Override
    public void observeReward(double reward, ArrayList<Position> others) {
        predPos = new Position(others.get(0));
    }
    
    /**
     * The policy is learned here
     * @param sweeps = number of sweeps executed
     * @throws OptimizationException 
     */
    
    public void learn(int sweeps) throws OptimizationException{
        largestDif = 0.0;
        //temporary table to store new V-values
        StateRepV newV = new StateRepV(init, true);
        for(int i =0;i<sweeps;i++){            
            double diff = 0.0;
            // for each state
            for(int state = 0; state<StateRepV.nrStates;state++){
                // solve the set of equations
                double[] values = solveEquations(state);
                //calculate difference in v-value
                if(state!=0){
                    diff = Math.abs(vValues.getV(state)-values[Action.nrActions]);
                    if(diff>largestDif){
                        largestDif = diff;
                    }
                    newV.setValue(state, values[Action.nrActions]);
                }
                // repair values returned by solving the equations if neccessary 
                for(int a = 0; a<Action.nrActions;a++){  
                    if(values[a]<0.00000001){
                        policy.setValue(state, Action.getAction(a), 0.00000001) ;
                    }
                    else if(values[a]>0.99999999){                        
                        policy.setValue(state, Action.getAction(a), 1.0) ;
                    }
                    else{
                        policy.setValue(state, Action.getAction(a),values[a]) ;
                    }
                }
            }   
            // put new values in V-table
            vValues = newV;
            newV = new StateRepV(init, true);            
        }
    }

    /**
     * Maximize V while not violating the constraints
     * @param state = state the maximization has to take place for
     * @return = array with values for pi's and v
     * @throws OptimizationException 
     */
    
    private double[] solveEquations(int state) throws OptimizationException {
        Collection constraints = new ArrayList();
        //for each possible action of the prey
        for(int preyAction=0;preyAction<Action.nrActions;preyAction++){
            //initialize weigths for this constraint
            double[] Q = new double[Action.nrActions+1];
            //for each possible action of the predator
            for(int predAction = 0; predAction<Action.nrActions;predAction++){
                int newStatePred = policy.getLinearIndexForAction(state, Action.getReverseAction(predAction));
                int newStatePrey = policy.getLinearIndexForAction(newStatePred, Action.getAction(preyAction));
                //calculate expected reward R(s,a,o)
                double expReward = 0;
                if(preyAction == Action.Wait.getIntValue()){
                    expReward = policy.getReward(newStatePrey, true);
                }
                else{
                    expReward = policy.getReward(newStatePrey, true)*(1.0-Ptrip)+policy.getReward(newStatePred, true)*Ptrip;
                }
                //Calculate Q for this combination
                if(preyAction == Action.Wait.getIntValue()){
                    Q[predAction] = expReward+learningRate*vValues.getV(newStatePrey);
                }
                else{
                    Q[predAction] = expReward+learningRate*vValues.getV(newStatePrey)*(1.0-Ptrip)+learningRate*vValues.getV(newStatePred)*Ptrip;
                } 
            }
            //add constraint weight for V
            Q[Action.nrActions] = -1.0;
            constraints.add(new LinearConstraint(Q, Relationship.GEQ, 0));
        }
        
        //add constraints that probabilities need to be > 0
        for(int predAction = 0; predAction<Action.nrActions;predAction++){
                double[] constraintProb = new double[Action.nrActions+1];
                Arrays.fill(constraintProb,0.0);
                constraintProb[predAction] = 1.0;
                constraints.add(new LinearConstraint(constraintProb, Relationship.GEQ, 0));
        }
        //add total is zero constraint
        double[] totalZero = new double[Action.nrActions+1];
        Arrays.fill(totalZero,1.0);
        totalZero[Action.nrActions] = 0.0;
        constraints.add(new LinearConstraint(totalZero, Relationship.EQ, 1.0));
        //build objective function
        double[] objective = new double[Action.nrActions+1];
        Arrays.fill(objective,0.0);
        objective[Action.nrActions] = 1.0;
         LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0);
        
        //solve and return
        RealPointValuePair solution = new SimplexSolver().optimize(f, constraints, GoalType.MAXIMIZE, false);
        return solution.getPoint();
    }
    

    /**
     * Returns the probability distribution over actions for a given state in the environment
     * @param prey = position of the prey
     * @param predatorItself = position of the predator
     * @return probability distribution over actions
     */
    @Override
    public double[] policy(Position prey, Position predatorItself) {
        double[] pActions = new double[Action.nrActions];
        int linIndex = vValues.getLinearIndex(prey, predatorItself);
        for(int i = 0;i<Action.nrActions;i++){   
            int index = vValues.getMove(predatorItself, prey, i, false);
            pActions[index] = policy.getValue(linIndex, Action.getAction(i));
        }            
        return pActions;
    }
    
    /**
     * returns the v-matrix of this agent
     * @return V
     */
    public double [][] getVMatrix(){
        return vValues.getMatrix();
    }

    /**
     * prints v- matrix of this agent
     * @param latex = boolean if it should be latex style or not
     */
    public void printV(boolean latex){
        vValues.printAll(latex);
    }
    
    /**
     * returns largest difference in v-values
     * @return largest difference
     */
    public double getLargestDiff(){
        return largestDif;
    }
    
    /**
     * erases what the agent has learned for reuse purposes
     */
    public void forgetLearning(){
        policy = new RelativeStateRep(1.0/Action.nrActionsDouble);
        vValues = new StateRepV(init, false);
    }
    
        /**
     * function that outputs a constraint for debugging purpose
     * @param Q = the constraint weights
     * @param isConstraint = boolean that determines what needs to be printed:
     *  the objective function or a constraint
     * @param eqZero = boolean determining if the constraint is an equal to 1 or larger or equal to 0 constraint
     */
    private void printEquation(double[] Q, boolean isConstraint, boolean eqZero){
        if(isConstraint){
            for(int i = 0; i<Q.length-1;i++){
                System.out.print(Q[i]+" pi" + i + " + ");
            }
            if(eqZero){
                System.out.println(Q[Q.length-1]+"V = 1");
            }
            else{
                System.out.println(Q[Q.length-1]+"V >= 0");
            }
        }
        else{
            System.out.println("Maximize: ");
            for(int i = 0; i<Q.length-1;i++){
                System.out.print(Q[i]+" pi" + i + " + ");
            }
            System.out.println(Q[Q.length-1]+"V\n");
        }
        
    }

    /**
     * prints solution after maximizing v without violating constraints for debugging purpose
     * @param solution = the solution
     * @param nrVar = the number of variables in the solution
     */
    private void printSolution(RealPointValuePair solution, int nrVar) {
        for(int i = 0;i<nrVar;i++){
            System.out.print("pi"+i+"="+solution.getPoint()[i]+" ");
        }
        System.out.println(" V = " + solution.getValue());
        System.out.println(" V = " + solution.getPoint()[nrVar]+"\n\n");
    }
}

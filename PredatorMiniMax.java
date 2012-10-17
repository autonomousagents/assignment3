
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.linear.LinearConstraint;
import org.apache.commons.math.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math.optimization.linear.Relationship;
import org.apache.commons.math.optimization.linear.SimplexSolver;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lyltje
 */
public class PredatorMiniMax implements Agent {

    private static final double Ptrip = 0.2;
    private Position startPos, myPos;
    StateRepQMinimax policy;
    StateRepV vValues;
    Position startPrey;
    Position preyPos;
    int nrRuns, maxNrRuns;
    double learningRate;
    double epsilon;
    
    public PredatorMiniMax(Position startPos, Position preyStartPos, double init, double learningRate, double epsilon){
        this.startPos = startPos;
        myPos = new Position(startPos);
        startPrey = preyStartPos;
        this.preyPos = new Position(preyStartPos);
        policy = new StateRepQMinimax(1.0/Action.nrActionsDouble, false);
        vValues = new StateRepV(init, false);
        this.learningRate = learningRate;
        this.epsilon = epsilon;
    }
    
    @Override
    public void doMove(ArrayList<Position> others) {
        preyPos = new Position(others.get(0));
        int linIndex = policy.getLinearIndex(preyPos, myPos);
        double [] prob = policy.getStateActionPairValues(linIndex);
        double [] probCum = new double [Action.nrActions];
        probCum[0] = prob[0];
        for(int i = 1; i<Action.nrActions-1;i++){
            probCum[i] = probCum[i-1]+prob[i];
        }
        probCum[Action.nrActions-1] = 1.0;
        double p = Math.random();
        int action = -1;
        for(int i = 1; i<Action.nrActions-1;i++){
            if(p<=probCum[i]){
                action = i;
                break;
            }
        }
        myPos.adjustPosition(policy.getMove(myPos, preyPos, action, false));
    }

    @Override
    public Position getPos() {
        return myPos;
    }

    @Override
    public void reset() {
        myPos = new Position(startPos);
        preyPos = new Position(startPrey);
    }

    @Override
    public boolean isConverged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void observeReward(double reward, ArrayList<Position> others) {
        
    }
    public void learn() throws OptimizationException{
        boolean notConverged = true;
        int sweep = 0;
        while(notConverged){
            sweep++;
            if(sweep%100==0){
                System.out.println("sweep number: " + sweep);                
            }
            double largestDiff = 0.0;
            double diff = 0.0;
            for(int state = 0; state<StateRepV.nrStates;state++){
//                System.out.println("state nr: " + state);
                double[] values = solveEquations(state);
                if(state!=0){
                    diff = Math.abs(vValues.getV(state)-values[Action.nrActions]);
                    vValues.setValue(state, values[Action.nrActions]);
                }
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
                    if(diff>largestDiff){
                        largestDiff = diff;
                    }
                }
                notConverged = largestDiff>epsilon || sweep<500;
            }            
        }
    }

    private double[] solveEquations(int state) throws OptimizationException {
        Collection constraints = new ArrayList();
        //for each possible action of the prey
        for(int preyAction=0;preyAction<Action.nrActions;preyAction++){
            //initialize weigths for this constraint
            double[] Q = new double[Action.nrActions+1];
            //for each possible action of the predator
            for(int predAction = 0; predAction<Action.nrActions;predAction++){
                int newStatePred = policy.getLinearIndexForAction(state, Action.getAction(predAction ));
                int newStatePrey = policy.getLinearIndexForAction(newStatePred, Action.getAction(preyAction));
                //calculate expected reward R(s,a,o)
                double expReward = 0;
                if(preyAction == Action.Wait.getIntValue()){
                    expReward = policy.getReward(newStatePrey);
                }
                else{
                    expReward = policy.getReward(newStatePrey)*(1.0-Ptrip)+policy.getReward(newStatePred)*Ptrip;
                }
                //add weight to constraint for this combitnation 
                if(preyAction == Action.Wait.getIntValue()){
                    Q[predAction] = expReward+learningRate*vValues.getV(newStatePrey);
                }
                else{
                    Q[predAction] = expReward+learningRate*vValues.getV(newStatePrey)*(1.0-Ptrip)+learningRate*vValues.getV(newStatePred)*Ptrip;
                } 
            }
            //add constraint weight for V
            Q[Action.nrActions] = -1.0;
            ///print constraint
//            printEquation(Q, true, false);
            //add constraint
            constraints.add(new LinearConstraint(Q, Relationship.GEQ, 0));
        }
        
        //add constraints that probabilities need to be > 0
        for(int predAction = 0; predAction<Action.nrActions;predAction++){
                double[] constraintProb = new double[Action.nrActions+1];
                Arrays.fill(constraintProb,0.0);
                constraintProb[predAction] = 1.0;
//                printEquation(constraintProb, true, false);
                constraints.add(new LinearConstraint(constraintProb, Relationship.GEQ, 0));
        }
        //add total is zero constraint
        double[] totalZero = new double[Action.nrActions+1];
        Arrays.fill(totalZero,1.0);
        totalZero[Action.nrActions] = 0.0;
        constraints.add(new LinearConstraint(totalZero, Relationship.EQ, 1.0));
//        printEquation(totalZero, true, true);
        //build objective function
        double[] objective = new double[Action.nrActions+1];
        Arrays.fill(objective,0.0);
        objective[Action.nrActions] = 1.0;
//        printEquation(objective, false, false);
         LinearObjectiveFunction f = new LinearObjectiveFunction(objective, 0);
        
        //solve and return
        RealPointValuePair solution = new SimplexSolver().optimize(f, constraints, GoalType.MAXIMIZE, false);
//        System.out.println("solution: ");
//        printSolution(solution, Action.nrActions);
        return solution.getPoint();
    }
    
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

    private void printSolution(RealPointValuePair solution, int nrVar) {
        for(int i = 0;i<nrVar;i++){
            System.out.print("pi"+i+"="+solution.getPoint()[i]+" ");
        }
        System.out.println(" V = " + solution.getValue());
        System.out.println(" V = " + solution.getPoint()[nrVar]+"\n\n");
    }

    @Override
    public double[] policy(Position prey, Position predatorItself, ArrayList<Position> others) {
        double[] pActions = new double[Action.nrActions];
        int linIndex = vValues.getLinearIndex(prey, predatorItself);
        for(int i = 0;i<Action.nrActions;i++){   
            int index = vValues.getMove(predatorItself, prey, i, false);
            pActions[index] = policy.getValue(linIndex, Action.getAction(i));
        }            
        return pActions;
    }
    
    public double [][] getVMatrix(){
        return vValues.getMatrix();
    }

    public void printV(boolean latex){
        vValues.printAll(latex);
    }

    
}

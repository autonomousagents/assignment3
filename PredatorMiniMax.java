
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
    
    public PredatorMiniMax(Position startPos, Position preyStartPos, double init, double learningRate){
        this.startPos = startPos;
        myPos = new Position(startPos);
        startPrey = preyStartPos;
        this.preyPos = new Position(preyStartPos);
        policy = new StateRepQMinimax(1.0/Action.nrActionsDouble, false);
        vValues = new StateRepV(init, false);
        this.learningRate = learningRate;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] policy(Position prey, ArrayList<Position> others) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void learn() throws OptimizationException{
        boolean notConverged = true;
        while(notConverged){
            for(int state = 0; state<StateRepV.nrStates;state++){
                double[] values = solveEquations(state);
                vValues.setValue(state, values[Action.nrActions]);
                for(int a = 0; a<Action.nrActions;a++){                    
                    policy.setValue(state, Action.getAction(a), values[a]) ;
                }
            }            
        }
    }

    private double[] solveEquations(int state) throws OptimizationException {
        Collection constraints = new ArrayList();
        for(int preyAction=0;preyAction<Action.nrActions;preyAction++){
            double[] Q = new double[Action.nrActions+1];
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
                    Q[predAction] = expReward+vValues.getV(newStatePrey);
                }
                else{
                    Q[predAction] = expReward+vValues.getV(newStatePrey)*(1.0-Ptrip)+vValues.getV(newStatePred)*Ptrip;
                }
            }
            //add constraint weight for V
            Q[Action.nrActions] = -1.0;
            //add constraint
            constraints.add(new LinearConstraint(Q, Relationship.GEQ, 0));
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
    
}


import java.util.ArrayList;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 3
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */

public class StateRep {

    private int nrOtherAgents;
    private double initialValue;
    private Matrix2DReduced outerPredator; // recursieve structuur
    private RelativeStateRep onePredator; // als er maar één predator is

    public StateRep(double init, int nrPredators) {

       
        this.nrOtherAgents = nrPredators - 1;
        this.initialValue = init;

        initStates();
    }

    /**
     * create the layers of matrices  and set all values and "initialValue"
     */
    private void initStates() {
        if (nrOtherAgents > 0) {
            outerPredator = new Matrix2DReduced(Environment.HEIGHT, Environment.WIDTH);
            outerPredator.init(nrOtherAgents, initialValue);
        }
        else {
           onePredator = new RelativeStateRep(initialValue);
        }
    }

    
    /**
     * Return action of enum Action based on corresponding integer
     * e.g. when actionNumber is 1, it returns HorizontalRetreat
     */
    public Action returnAction(int actionNumber) {
        return RelativeStateRep.returnAction(actionNumber);
    }


    /**
     * Set value for an agent given his own Position, all other's Positions, and his own Action
     *
     * in allOtherPositions, prey's position should be at index 0 !!!
     * (except when the Agent in question, is the prey, then it should be it's "reference predator")
     */
    public void setActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, double value) {
        if (nrOtherAgents > 0) {
            outerPredator.setActionValue(myPos, allOtherPositions, myAction, nrOtherAgents, value);
        }
        else {
             int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));
             onePredator.setValue(lIndex, myAction, value);
        }

    }


    /**
     * Get value for an agent given his own Position, all other's Positions, and his own Action
     *
     * in allOtherPositions, prey's position should be at index 0 !!!
     * (except when the Agent in question, is the prey, then it should be it's "reference predator")
     */
    public double getActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction) {
        if (nrOtherAgents > 0) {
            return outerPredator.getActionValue(myPos, allOtherPositions, myAction, nrOtherAgents);
        }
        else {
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));
            return onePredator.getValue(lIndex, myAction);
        }
    }


    /**
     * Get all action-values (= array of size Action.nrActions) for an agent given his own Position, all other's Positions, and his own Action
     *
     * previously called "getStateActionPairValues"
     *
     * in allOtherPositions, prey's position should be at index 0 !!!
     * (except when the Agent in question, is the prey, then it should be it's "reference predator")
     */
    public double[] getAllActionValues(Position myPos, ArrayList<Position> allOtherPositions) {
         if (nrOtherAgents > 0) {
            return outerPredator.getAllActionValues(myPos, allOtherPositions, nrOtherAgents);
        }
         else {
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));
            return onePredator.getStateActionPairValues(lIndex);
         }
    }


    /**
     * Returns integer corresponding to real world move (e.g. 0 == UP, 1 == RIGHT) based on
     * action in state representation (e.g. HorzontalRetreat) and
     * position of this agent and all other agent's positions
     *
     * in allOtherPositions, prey's position should be at index 0 !!!
     * (except when the Agent in question, is the prey, then it should be it's "reference predator")
     *
     * @param myPos : this agents positions
     * @param allOtherPositions : positions of other agents
     * @param stateRepAction = action in state space, from the enum Action (e.g. HorizontalRetreat)
     * @return integer corresponding to action in real world (e.g. 0 == UP, 1 == RIGHT)
     */
    public int getMove(Position myPos, ArrayList<Position> allOtherPositions, Action stateRepAction) {

        boolean print = false; // set to true if you want to debug

         if (nrOtherAgents > 0) {
             return outerPredator.getMove(myPos, allOtherPositions, stateRepAction, nrOtherAgents, print);
        }
         else {
                return onePredator.getMove(myPos, allOtherPositions.get(0), stateRepAction, print);
         }
    }

    /**
     * Debug function
     */
    public void test() {

        ArrayList<Position> otherPositions = new ArrayList<Position>();

        Position myPos = new Position(3, 3);

        Position preyPos = new Position(2, 1);
        otherPositions.add(preyPos);

        Position otherPredatorPos = new Position(1, 1);
        for (int i = 0; i < nrOtherAgents; i++) {
            otherPositions.add(otherPredatorPos);
        }

        setActionValue(myPos, otherPositions, Action.VerticalApproach,  5);

//         System.out.println(outerPredator.getActionValue(myPos, otherPositions, Action.HorizontalApproach, nrOtherPredators));
//         System.out.println(outerPredator.getActionValue(new Position(1,2), otherPositions, Action.HorizontalApproach, nrOtherPredators));
//
//         otherPositions.set(nrOtherPredators-1, new Position(5,6));
//         System.out.println(outerPredator.getActionValue(myPos, otherPositions, Action.HorizontalApproach, nrOtherPredators));

        double[] values = getAllActionValues(myPos, otherPositions);
        for (int i = 0; i < Action.nrActions; i++) {
            System.out.print(values[i] + "  ");
        }
        System.out.println();

        System.out.println(getMove(myPos, otherPositions, Action.HorizontalApproach));
    }
}

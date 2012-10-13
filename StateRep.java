
import java.util.ArrayList;


public class StateRep {
	
	private boolean isPrey;
	private int nrOtherPredators;	
	private double initialValue;

    private Matrix2D outerPredator; // recursieve structuur
	
	
	 public StateRep (double init, boolean isPrey, int nrPredators ){     
		 
		 this.isPrey = isPrey;	 		
		 this.nrOtherPredators = nrPredators-1;
		 this.initialValue = init;

         initStates();		
    }

     /**
      * create the layers of matrices  and set all values and "initialValue"
      */
	 private void initStates() {
         outerPredator = new Matrix2D(Environment.HEIGHT, Environment.WIDTH);
         outerPredator.init(nrOtherPredators, initialValue);
	 }
    

     /**
      * e.g. when actionNumber is 1, it returns HorizontalRetreat
      */
     public Action returnAction(int actionNumber) {
         return RelativeStateRep.returnAction(actionNumber);
     }

     /**
      * set value for a predator given his own Position, all other's Positions, and his own Action
      *
      * in allOtherPositions, prey's position should be at index 0 !!!
      * (except when the Agent in question, is the prey, then it should be it's "reference predator")
      */
     public void setActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, double value) {
        outerPredator.setActionValue(myPos, allOtherPositions, myAction, nrOtherPredators, value);
     }

     /**
      * get value for a predator given his own Position, all other's Positions, and his own Action
      *
      * in allOtherPositions, prey's position should be at index 0 !!!
      * (except when the Agent in question, is the prey, then it should be it's "reference predator")
      */
     public double getActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction) {
         return outerPredator.getActionValue(myPos, allOtherPositions, myAction, nrOtherPredators);
     }

     /**
      * get all action-values (= array of size Action.nrActions) for a predator given his own Position, all other's Positions, and his own Action
      *
      * previously called "getStateActionPairValues"
      * 
      * in allOtherPositions, prey's position should be at index 0 !!!
      * (except when the Agent in question, is the prey, then it should be it's "reference predator")
      */
     public double[] getAllActionValues(Position myPos, ArrayList<Position> allOtherPositions) {
          return outerPredator.getAllActionValues(myPos, allOtherPositions, nrOtherPredators);
     }


    /**
    * Returns integer corresponding to real world move (e.g. 0 == UP, 1 == RIGHT) based on action in state representation and position of
    * this agent and all other positions
    *
    * in allOtherPositions, prey's position should be at index 0 !!!
    * (except when the Agent in question, is the prey, then it should be it's "reference predator")
    *
    * @param myPos : this agents positions
    * @param allOtherPositions : positions of other agents
    * @param stateRepAction = action in state space, from the enum Action (e.g. HorizontalRetreat)
    * @return integer corresponding to action in real world (e.g. 0 == UP, 1 == RIGHT)
    */
    public int getMove(Position myPos, ArrayList<Position> allOtherPositions, Action stateRepAction, boolean print) {

        return outerPredator. getMove( myPos, allOtherPositions,  stateRepAction, nrOtherPredators,  print) ;

    }

	 
	 public void test() {
         
         ArrayList<Position> otherPositions = new ArrayList<Position>();

         Position myPos = new Position(3,3);

         Position preyPos = new Position(2,1);
         otherPositions.add(preyPos);

         Position otherPredatorPos = new Position(1,1);
         for (int i=0; i < nrOtherPredators; i++ )
               otherPositions.add(otherPredatorPos);

         outerPredator.setActionValue(myPos, otherPositions, Action.VerticalApproach, nrOtherPredators,5);
         
//         System.out.println(outerPredator.getActionValue(myPos, otherPositions, Action.HorizontalApproach, nrOtherPredators));
//         System.out.println(outerPredator.getActionValue(new Position(1,2), otherPositions, Action.HorizontalApproach, nrOtherPredators));
//
//         otherPositions.set(nrOtherPredators-1, new Position(5,6));
//         System.out.println(outerPredator.getActionValue(myPos, otherPositions, Action.HorizontalApproach, nrOtherPredators));

         double[] values = getAllActionValues(myPos,otherPositions);
         for (int i=0; i < Action.nrActions; i++)
             System.out.print(values[i] + "  ");
         System.out.println();
     }
	    
}

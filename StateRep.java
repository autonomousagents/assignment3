
import java.util.ArrayList;


public class StateRep {
	
	private boolean isPrey;
	private int nrOtherPredators;	
	private double initialValue;

    private Matrix2D outerPredator; // recursieve structuur
	
	
	 public StateRep (double init, boolean isPrey, int nrPredators ){
       // stateRep = new double[stateRepHeight][stateRepWidth][nrActions];
      //  for(double[][] m: stateRep)
        //	for(double[] row: m)Arrays.fill(row, init);
      //  fillUnused();
		 
		 this.isPrey = isPrey;	 		
		 this.nrOtherPredators = nrPredators-1;
		 this.initialValue = init;

         initStates();
		
    }
	 
	 private void initStates() {

         outerPredator = new Matrix2D(Environment.HEIGHT, Environment.WIDTH);
         outerPredator.init(nrOtherPredators, initialValue);
	 }

     

     /**
      * e.g. 1 = HorizontalRetreat
      */
     public Action returnAction(int actionNumber) {
         return RelativeStateRep.returnAction(actionNumber);
     }

     /**
      * in allOtherPositions, prey's position should be at index 0 !!!
      */
     public void setActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, double value) {
        outerPredator.setActionValue(myPos, allOtherPositions, myAction, nrOtherPredators, value);
     }

     /**
      * in allOtherPositions, prey's position should be at index 0 !!!
      */
     public double getActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction) {
         return outerPredator.getActionValue(myPos, allOtherPositions, myAction, nrOtherPredators);
     }

     /**
      * previously called "getStateActionPairValues"
      * 
      * in allOtherPositions, prey's position should be at index 0 !!!
      */
     public double[] getAllActionValues(Position myPos, ArrayList<Position> allOtherPositions, int level) {
          return outerPredator.getAllActionValues(myPos, allOtherPositions, nrOtherPredators);
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

         double[] values = getStateActionPairValues(myPos,otherPositions);
         for (int i=0; i < Action.nrActions; i++)
             System.out.print(values[i] + "  ");
         System.out.println();
     }
	    
}

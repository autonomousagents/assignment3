
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

     public double[] getStateActionPairValues(Position myPos, ArrayList<Position> otherPositions) {
        return outerPredator.getAllActionValues(myPos, otherPositions,nrOtherPredators);
    }

	 
	 public void test() {
         
         ArrayList<Position> otherPositions = new ArrayList<Position>();

         Position myPos = new Position(3,3);

         Position preyPos = new Position(2,1);
         otherPositions.add(preyPos);

         Position otherPredatorPos = new Position(1,1);
         for (int i=0; i < nrOtherPredators+1; i++ )
               otherPositions.add(otherPredatorPos);

         outerPredator.setActionValue(myPos, otherPositions, Action.HorizontalApproach, nrOtherPredators,5);
         
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

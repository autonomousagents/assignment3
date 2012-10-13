
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

         fillOtherPredators();
		
    }
	 
	 private void fillOtherPredators() {

         outerPredator = new Matrix2D(Environment.HEIGHT, Environment.WIDTH);
         outerPredator.init(nrOtherPredators, initialValue);

       
	 }
	 
	 public void test() {
         Position bla = new Position(1,1);
         ArrayList<Position> otherPositions = new ArrayList<Position>();
         for (int i=0; i < nrOtherPredators+1; i++ )
               otherPositions.add(bla);

         System.out.println(outerPredator.get(bla, otherPositions, Action.HorizontalApproach, nrOtherPredators));
     }
	    
}

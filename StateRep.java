import java.util.ArrayList;


public class StateRep {
	
	private boolean isPrey;
	private int nrOtherPredators;
	
	//private double[][][] oneOtherPredator; //[Environment.HEIGHT][Environment.WIDTH][Direction.nrMoves];
	private ArrayList<double[][][]> otherPredators;
	
	 public StateRep (boolean isPrey, int nrPredators ){
       // stateRep = new double[stateRepHeight][stateRepWidth][nrActions];
      //  for(double[][] m: stateRep)
        //	for(double[] row: m)Arrays.fill(row, init);
      //  fillUnused();
		 
		 this.isPrey = isPrey;
		 
		 if (isPrey)
			 this.nrOtherPredators = nrPredators;
		 else
			 this.nrOtherPredators = nrPredators-1;
		 
		
    }
	 
	 private void fillOtherPredators() {
		 otherPredators = new ArrayList<double[][][]>();
		 
		 for (int i =0; i < nrPredators; i++) {
			 double[][][] otherPredator = new double[Environment.HEIGHT][Environment.WIDTH][Direction.nrMoves];
			 otherPredators.add(otherPredator)		;	 
		 }
	 }
	 
	 
	    
}

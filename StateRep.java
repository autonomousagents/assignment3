
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
		 
		
    }
	 
	 private void fillOtherPredators() {

         outerPredator.init(nrOtherPredators, initialValue);
	 }
	 
	 
	    
}

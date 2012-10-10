import java.util.ArrayList;


public class StateRep {
	
	private boolean isPrey;
	private int nrOtherPredators;
	private RelativeStateRep relativeStateRep; // own position w.r.t. prey, or (if himself is prey) w.r.t. first predator
	private double initialValue;
	
	//private double[][][] oneOtherPredator; //[Environment.HEIGHT][Environment.WIDTH][Direction.nrMoves];
	private ArrayList<double[][][]> otherPredators;
	
	 public StateRep (double init, boolean isPrey, int nrPredators ){
       // stateRep = new double[stateRepHeight][stateRepWidth][nrActions];
      //  for(double[][] m: stateRep)
        //	for(double[] row: m)Arrays.fill(row, init);
      //  fillUnused();
		 
		 this.isPrey = isPrey;	 
		
		 this.nrOtherPredators = nrPredators-1;
		 this.initialValue = init;
		 
		 relativeStateRep = new RelativeStateRep(initialValue);
		
    }
	 
	 private void fillOtherPredators() {
		 otherPredators = new ArrayList<double[][][]>();
		 
		 for (int i =0; i < nrOtherPredators; i++) {
			 double[][][] otherPredator = new double[Environment.HEIGHT][Environment.WIDTH][Direction.nrMoves];
			 otherPredators.add(otherPredator)	;	 
		 }
	 }
	 
	 
	    
}

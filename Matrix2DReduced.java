
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class Matrix2DReduced {

    private static final int maxLinearIndex = 120;
	private int nRows;
    private int nCols;
    // he only has/uses ONE of these, so perhaps create an interface/superclass for Matrix2D and RelativeStateRep
    private Matrix2DReduced[][] innerMatrix2D;
    private RelativeStateRep[][] bottomMatrix;

    public Matrix2DReduced(int nrows, int ncols) {
        nRows = nrows;
        nCols = ncols;
    }

    public Matrix2DReduced() {
        nRows = Environment.HEIGHT;
        nCols = Environment.WIDTH;
    }

    /**
     * e.g., when there are 4 predators
     * level:  0 = prey and this predator, 1 = first other predator, 2 = second other predator, 3 = third other predator
     */
    public void init(int level, double initValue) {
    	initMatrices(level, initValue);
    	leveledStateAssignment(level, 0, new ArrayList<Position>(), initValue);
    }
    
    private void initMatrices(int level, double initValue) {

      
        //  System.out.println(level);
        
        if (level == 1) { // first other predator

            bottomMatrix = new RelativeStateRep[nRows][nCols]; // bevat 2D matrix met daarin RelativeStateRep's (voor prey and this predator)

//            for (int i = 0; i < nRows; i++) {
//                for (int j = 0; j < nCols; j++) {
//                    bottomMatrix[i][j] = new RelativeStateRep(initValue);
//                }
//            }
        }
        else {

            innerMatrix2D = new Matrix2DReduced[nRows][nCols]; // bevat 2D matrix met daarin Matrix2D's  (voor volgende andere predator)

            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < nCols; j++) {
                    innerMatrix2D[i][j] = new Matrix2DReduced(nRows, nCols);
                    innerMatrix2D[i][j].init(level - 1, initValue); // spreek deze functie wee aan met level-1
                }
            }
        }
    }

    //level = predators -1 && predators > 1
    private void leveledStateAssignment (int level, int linearIndex, ArrayList<Position> posArray, double initValue){
    	//until bottom is reached, then fill the corresponding posarray combinations with state.
    	if (level == 0) {
    		RelativeStateRep state = new RelativeStateRep(initValue);
    		reduceStates(posArray, state);
    		return;
    	}
    	//for each level give along the corresponding pos
    	for(int i = linearIndex; i <= maxLinearIndex;i++){
    		@SuppressWarnings("unchecked")
			ArrayList<Position> tempPosArray = (ArrayList<Position>) posArray.clone();
    		tempPosArray.add(linearIndexToPos(linearIndex));
    		leveledStateAssignment(level-1, linearIndex, tempPosArray, initValue);
    	}
    }
    
    private Position linearIndexToPos(int linearIndex) {
    	int x = linearIndex % nCols;
    	int y = (linearIndex - x) / nCols;
		return new Position(x,y);
	}

	@SuppressWarnings("unchecked")
	private void reduceStates (ArrayList<Position> posArray, RelativeStateRep state){
    	if(posArray.size() == 1) {
    		Position pos = posArray.get(0);
    		bottomMatrix[pos.getX()][pos.getY()] = state;
    	}
    	else {
    		for (int i = 0;i<= posArray.size();i++){
    			Position pos = posArray.get(i);
				ArrayList<Position> tempArray = (ArrayList<Position>) posArray.clone();
				tempArray.remove(i);
				innerMatrix2D[pos.getX()][pos.getY()].reduceStates(tempArray, state);
    		}
    	}
    }
    
    
    /**
     * e.g., when there are 4 predators
     * level:
     *      0 = prey and this predator, 1 = first other predator, 2 = second other predator, 3 = third other predator
     * in allOtherPositions:
     *      0 = preyPos,1 = first other predator's position, 2 = second other predator's position, 3 = third other predator's position
     */
    public double getActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, int level) {

        if (level == 1) { // ask from RelativeStateRep
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));

            return bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getValue(lIndex, myAction);
        }
        else {
            // ga bij innerlijke matrix opvragen (via deze zelfde functie, level-1)
            return innerMatrix2D[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getActionValue(myPos, allOtherPositions, myAction, level - 1);
        }
    }

    
    public double[] getAllActionValues(Position myPos, ArrayList<Position> allOtherPositions, int level) {

        if (level == 1) { // ask from RelativeStateRep
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));

            return bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getStateActionPairValues(lIndex);
        }
        else {
            // ga bij innerlijke matrix opvragen (via deze zelfde functie, level-1)
            return innerMatrix2D[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getAllActionValues(myPos, allOtherPositions, level - 1);
        }
    }


    public void setActionValue(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, int level, double value) {

        if (level == 1) { // set in RelativeStateRep
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));

            bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].setValue(lIndex, myAction, value);
        }
        else {
            // ga bij innerlijke matrix setten (via deze zelfde functie, level-1)
            innerMatrix2D[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].setActionValue(myPos, allOtherPositions, myAction, level - 1, value);
        }
    }


    public int getMove(Position myPos, ArrayList<Position> allOtherPositions, Action stateRepAction, int level, boolean print) {

        if (level == 1) { //get from RelativeStateRep

            return bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getMove(myPos, allOtherPositions.get(0), stateRepAction, print);
        }
        else {
            // ga bij innerlijke matrix setten (via deze zelfde functie, level-1)
            return innerMatrix2D[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()].getMove(myPos, allOtherPositions, stateRepAction, level - 1, print);
        }

    }
}

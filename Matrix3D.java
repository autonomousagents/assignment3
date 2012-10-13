
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class Matrix3D<T> {

    private int nRows;
    private int nCols;
    private int nActions;

    private Matrix3D[][][] matrix;
    private RelativeStateRep[][][] bottomMatrix;

    public Matrix3D(int nrows, int ncols, int nactions) {
        nRows=nrows;
        nCols=ncols;
        nActions=nactions;

        matrix = new Matrix3D[nRows][nCols][nActions];
    }

    public Matrix3D() {
        nRows = Environment.HEIGHT;
        nCols = Environment.WIDTH;
        nActions = Direction.nrMoves;

    }

    /**
     * e.g., when there are 4 predators
     * level:  0 = prey and this predator, 1 = first other predator, 2 = second other predator, 3 = third other predator
     */
    public void init(int level, double initValue) {

        if (level==1) {

            bottomMatrix = new RelativeStateRep[nRows][nCols][nActions];

            for (int i=0; i < nRows; i++) {
                for(int j=0; j < nCols; j++) {
                    for (int k=0; k < nActions; k++)
                        bottomMatrix[i][j][k] = new RelativeStateRep(initValue);
                }
            }
        }
        else {

            matrix = new Matrix3D[nRows][nCols][nActions];
            
            for (int i=0; i < nRows; i++) {
                for(int j=0; j < nCols; j++) {
                    for (int k=0; k < nActions; k++)
                        matrix[i][j][k].init(level-1, initValue);
                }
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
    public double get(Position myPos, ArrayList<Position> allOtherPositions, Action myAction, int level) {
        
        if (level == 1 ) { // ask from RelativeStateRep
            int lIndex = RelativeStateRep.getLinearIndexFromPositions(myPos, allOtherPositions.get(0));

            return bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()][allActions.get(level).getIntValue()] . getValue(lIndex, allActions.get(1));
        }
        else {
          //  return matrix[allOtherPositions.get(level-1).getX()][allOtherPositions.get(level-1).getY()][allAallOtherPositionsctions.get(level-1).getIntValue()].get(allPositions, allActions, level-1);
        }

    }


}

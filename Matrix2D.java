
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class Matrix2D<T> {

    private int nRows;
    private int nCols;

    // hij heeft één van deze twee, dus mss beter superklasse maken
    private Matrix2D[][] innerMatrix2D;
    private RelativeStateRep[][] bottomMatrix;

    public Matrix2D(int nrows, int ncols) {
        nRows=nrows;
        nCols=ncols;               
    }

    public Matrix2D() {
        nRows = Environment.HEIGHT;
        nCols = Environment.WIDTH;
    }

    /**
     * e.g., when there are 4 predators
     * level:  0 = prey and this predator, 1 = first other predator, 2 = second other predator, 3 = third other predator
     */
    public void init(int level, double initValue) {

        if (level==1) { // first other predator

            bottomMatrix = new RelativeStateRep[nRows][nCols]; // bevat 2D matrix met daarin RelativeStateRep's (voor prey and this predator)

            for (int i=0; i < nRows; i++) {
                for(int j=0; j < nCols; j++) {                   
                        bottomMatrix[i][j] = new RelativeStateRep(initValue);
                }
            }
        }
        else {

            innerMatrix2D = new Matrix2D[nRows][nCols]; // bevat 2D matrix met daarin Matrix2D's  (voor volgende andere predator)
            
            for (int i=0; i < nRows; i++) {
                for(int j=0; j < nCols; j++) {
                        innerMatrix2D[i][j] = new Matrix2D(nRows, nCols);
                        innerMatrix2D[i][j].init(level-1, initValue); // spreek deze functie wee aan met level-1
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

            return bottomMatrix[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()]. getValue(lIndex, myAction);
        }
        else {
            // ga bij innerlijke matrix opvragen (via deze zelfde functie, level-1)
            return innerMatrix2D[allOtherPositions.get(level).getX()][allOtherPositions.get(level).getY()] . get(myPos, allOtherPositions, myAction, level-1);
        }

    }


}

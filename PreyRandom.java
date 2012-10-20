
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lyltje
 */
public class PreyRandom implements Agent {
    
    private static final double Ptrip = 0.2;
    private Position startPos, myPos;
    ArrayList<Position> startOthers;
    ArrayList<Position> others;
    int nrRuns;
    
    
    public PreyRandom(Position startPos, ArrayList<Position> startOthers){
        this.startPos =startPos;
        this.startOthers = startOthers;
        others = Position.deepCopyList(startOthers);        
        myPos = new Position(startPos);
        nrRuns=0;
    }
    
    @Override
    public void doMove(ArrayList<Position> others) {
        double p = Math.random();
        if(p>Ptrip){
            p = Math.random();
            double start = 1.0/Action.nrActions;
            for(int i=0;i< Action.nrActions;i++){
                if(p<start){
                    myPos.adjustPosition(i);
                    break;
                }
                start += 1.0/Action.nrActions;
            }
        }
    }

    @Override
    public Position getPos() {
        return myPos;
    }

    @Override
    public void reset() {
        myPos = new Position(startPos);
        others = Position.deepCopyList(startOthers);
        nrRuns++;
    }

    @Override
    public boolean isConverged() {
        return true;
    }

    /**
     *
     * @param reward
     * @param others
     */
    @Override
    public void observeReward(double reward, ArrayList<Position> others) {
        //Useless in case of a random agent
    }


    @Override
    public double[] policy(Position prey, Position predatorItself) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}

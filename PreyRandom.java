
import java.util.ArrayList;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 3
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */

public class PreyRandom implements Agent {
    
    private static final double Ptrip = 0.2;
    private Position startPos, myPos;
    ArrayList<Position> startOthers;
    ArrayList<Position> others;
    int nrRuns;
    
    /**
     * Constructor
     * @param startPos = starting position of the random prey
     * @param startOthers = array list with starting position of the predator (s)
     */
    public PreyRandom(Position startPos, ArrayList<Position> startOthers){
        this.startPos =startPos;
        this.startOthers = startOthers;
        others = Position.deepCopyList(startOthers);        
        myPos = new Position(startPos);
        nrRuns=0;
    }
    
    /**
     * Returns the move according to the policy of the random prey
     * @param others = array list of position of the predators
     */
    @Override
    public void doMove(ArrayList<Position> others, boolean isPrey) {
        double p = Math.random();
        // if the prey does not trip
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

    /**
     * Get position of the prey
     * @return position
     */
    @Override
    public Position getPos() {
        return myPos;
    }

    /**
     * resets the agent for a new run
     */
    @Override
    public void reset() {
        myPos = new Position(startPos);
        others = Position.deepCopyList(startOthers);
        nrRuns++;
    }

    /**
     * useless function but part of the interface Agent
     * @return 
     */
    @Override
    public boolean isConverged() {
        //Useless in case of a random agent
        return true;
    }

    /**
     * useless function here but part of the interface Agent
     * @param reward
     * @param others
     */
    @Override
    public void observeReward(double reward, ArrayList<Position> others) {
        //Useless in case of a random agent
    }

    /**
     * useless function here but part of the interface Agent
     * @param reward
     * @param others
     */
    @Override
    public double[] policy(Position prey, Position predatorItself) {
        //Useless in case of a random agent
        return null;
    }

    
}

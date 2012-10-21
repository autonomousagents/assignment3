
import java.util.ArrayList;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 3
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */

public class Environment {

    public final static int HEIGHT = 11;
    public final static int WIDTH = 11;
    private boolean isEnded;
    private ArrayList<Agent> predators;
    private Agent prey;
    public static final double maximumReward = 10;
    public static final double minimumReward = -10;
    public static final double normalReward = 0;
    private int nrSteps;
    
    public static final int maxPredators = 4;

    public static final int doRunNTLmax = 5000000;


    /**
     * constructor of the environment
     * @param predators = array list with the predators
     * @param prey = the prey
     */
    public Environment(ArrayList<Agent> predators, Agent prey) {
        this.isEnded = false;
        this.predators = predators;
        this.prey = prey;
        nrSteps = 0;
        if(predators.size()> Environment.maxPredators){
            System.out.println("too many predators");
        }
        if(predators.size()==0){
            System.out.println("WARNING: no predators");
        }
    }

    /**
     * Should be invoked for any  time step in an episode
     * Makes prey do move, makes predator do move, and then makes predator observe reward (and thus update values etc.)
     */
    public void nextTimeStep() {
        ArrayList<ArrayList<Position>> posOthers= new ArrayList<ArrayList<Position>>();
        for(int j = 0; j<predators.size()+1;j++){
            posOthers.add(positionsOthers(j));
        }
        for(int i = 1; i<predators.size()+1;i++){
            predators.get(i-1).doMove(posOthers.get(i));
        }
        prey.doMove(posOthers.get(0));       
        prey.observeReward(reward(true), posOthers.get(0));

        int agentNr = 0;
        for(Agent predator:predators){
            agentNr++;
            predator.observeReward(reward(false), positionsOthers(agentNr));
        }
        checkForEnd();
        nrSteps++;
    }
    
    /**
     * returns the positions of all agents except one
     * @param agent = agents whose position should not be returned
     * @return 
     */
    private ArrayList<Position> positionsOthers(int agent){
        ArrayList<Position> positions = new ArrayList<Position>();
        if(agent == 0){
            for(Agent a:predators){
                positions.add(a.getPos());
            }
        }
        else{
            positions.add(prey.getPos());
            for(int i =0;i<predators.size();i++){
                if(i!=agent-1){
                    positions.add(predators.get(i).getPos());
                }
            }
        }
        return positions;
    }



    /**
     * Reward function for the environment
     */
    public double reward(boolean isPrey) {
        //If there is a collision between two predators: 
        //return minimal reward for predators and maximal reward for prey
        for(int i = 0; i<predators.size()-1;i++){
            for(int j = i+1;j<predators.size();j++){
                if(predators.get(i).getPos().equals(predators.get(j).getPos())){
                    if(isPrey){
                        return maximumReward;
                    }
                    else{
                        return minimumReward;
                    }
                }
            }
        }
        //If one of the predators catches the prey
        //return minimal reward for the prey and maximal reward for the predators
        for(Agent pred:predators){
            if(pred.getPos().equals(prey.getPos())){
                if(isPrey){
                    return minimumReward;
                }
                else{
                    return maximumReward;
                }
            }
        }
        //If both cases above do not apply: return normal reward
        return normalReward;
    }   
    
    /**
     * returns the number of steps of a run
     * @return 
     */
    public int getNrSteps(){
        return nrSteps;
    }

    /**
     * erases the number of steps
     */
    public void resetNrSteps(){
        nrSteps = 0;
    }

    /**
     * do one run
     */
    public void doRun(){
        while(!isEnded){
            nextTimeStep();
        }
        reset();
    }
   
    /**
     * returns whether or not the run has ended
     * @return position
     */
        public boolean isEnded() {
        return isEnded;
    }

    /**
     * returns the position of the prey
     * @return position
     */
    public Position getPreyPos() {
        return prey.getPos();
    }

    /**
     * Checks if the run has ended
     * @return 
     */
    private  boolean checkForEnd() {
        if(reward(true) != 0){
            isEnded = true;
        }
        return isEnded;
    }

    /**
     * resets the environment for a new run
     */
    public void reset() {
        prey.reset();
        isEnded = false;
        for(Agent predator:predators){
            predator.reset();
        }
    }
    
    /**
     * returns position of a specific predator
     * @param predatorNr = predator for which the position is requested
     * @return position
     */
    public Position getPredatorPos(int predatorNr){
        return predators.get(predatorNr).getPos();
    }
    
    /**
     * returns whether or not a predator is standing on position p
     * @param p = position
     * @return boolean whether or not a predator is standing on position p
     */
    public boolean predatorStandsHere (Position p){
        for(int i = 0; i<predators.size();i++){
            if(predators.get(i).getPos().equals(p)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * returns the number of predators
     * @return nr of predators
     */
    public int nrOtherAgents() {
    	return predators.size();
    }

    /**
     * does a run with a maximum number of steps
     * @return maximal number of steps if that is reached or -1 if it terminated by itself
     */
    public int doRunNTL() {
        while(!isEnded){
            if(nrSteps < doRunNTLmax){
                nextTimeStep();
            }
            else {
                return doRunNTLmax;
            }
        }
        reset();
        return -1;        
    }
}

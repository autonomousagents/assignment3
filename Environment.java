
import java.util.ArrayList;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 1
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
        for(int i = 1; i==predators.size();i++){
            predators.get(i-1).doMove(posOthers.get(i));
        }
        prey.doMove(posOthers.get(0));
        int agentNr = 0;
        prey.observeReward(reward(true), positionsOthers(agentNr));
        for(Agent predator:predators){
            agentNr++;
            predator.observeReward(reward(false), positionsOthers(agentNr));
        }
        checkForEnd();
        nrSteps++;
    }
    
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
    
    public int getNrSteps(){
        return nrSteps;
    }

    public void resetNrSteps(){
        nrSteps = 0;
    }

    public void doRun(){
        while(!isEnded){
            nextTimeStep();
        }
        reset();
    }
    
//    public void doRunInvalid(){
//        boolean validRun = false;
//        int invalidRun = 0;
//        while(!validRun){
//            while(!isEnded){
//                if(nrSteps<10000000){
//                    nextTimeStep();
//                    if(isEnded){
//                        validRun = true;
//                    }
//                }
//                else{
//                    nrSteps = 0;
//                    System.out.println("invalid run" +invalidRun);
//                    invalidRun++;
//                    break;
//                }
//            }
//            reset();
//        }
//    }
    
        public boolean isEnded() {
        return isEnded;
    }

    public Position getPreyPos() {
        return prey.getPos();
    }



    private  boolean checkForEnd() {
        if(reward(true) != 0){
            isEnded = true;
        }
        return isEnded;
    }

    public void reset() {
        prey.reset();
        isEnded = false;
        for(Agent predator:predators){
            predator.reset();
        }
    }
    
    public Position getPredatorPos(int predatorNr){
        return predators.get(predatorNr).getPos();
    }
    
    public boolean predatorStandsHere (Position p){
        for(int i = 0; i<predators.size();i++){
            if(predators.get(i).getPos().equals(p)){
                return true;
            }
        }
        return false;
    }
    
    public int nrOtherAgents() {
    	return predators.size();
    }

    public int doRunNTL() {
        int max = 2000000;
        while(!isEnded){
            if(nrSteps<max){
                nextTimeStep();
            }
            else{                
                return max;
            }
        }
        reset();
        return -1;        
    }
}

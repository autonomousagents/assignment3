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
    private Agent predator;
    private Agent prey;
    public static final double maximumReward = 10;
    public static final double minimumReward = 0;
    public static final double normalReward = 0;
    int nrSteps;


    public Environment(Agent predator, Position preyStart) {
        this.isEnded = false;
        this.predator = predator;
        prey = new Prey(preyStart);
        nrSteps = 0;
    }

    /**
     * Should be invoked for any  time step in an episode
     * Makes prey do move, makes predator do move, and then makes predator observe reward (and thus update values etc.)
     */
    public void nextTimeStep() {
        predator.doMove(getPreyPos());
        prey.doMove(getPredatorPos());
        predator.observeReward(reward(prey.getPos(), predator.getPos()), prey.getPos());
        checkForEnd();
        nrSteps++;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public Position getPreyPos() {
        return prey.getPos();
    }

    public Position getPredatorPos() {
        return predator.getPos();
    }

    public  boolean checkForEnd() {
        isEnded = getPredatorPos().equals(getPreyPos());
        return isEnded;
    }

    public void reset() {
        prey = new Prey(new Position(5, 5));
        isEnded = false;
        predator.reset();
    }

    /**
     * Reward function for the environment
     */
    public static double reward(Position prey, Position predator) {
        if (prey.getX() == predator.getX() && prey.getY() == predator.getY()) {
            return maximumReward;
        }
        else {
            return normalReward;
        }
    }


    public int getNrSteps(){
        return nrSteps;
    }

    public void resetNrSteps(){
        nrSteps = 0;
    }

    public void doRun(){
        boolean validRun = false;
        int invalidRun = 0;
        while(!validRun){
            while(!isEnded){
                if(nrSteps<80000){
                    nextTimeStep();
                    nrSteps++;
                    if(isEnded){
                        validRun = true;
                    }
                }
                else{
                    nrSteps = 0;
                  //  System.out.println("invalid run" +invalidRun);
                    invalidRun++;
                    break;
                }
            }
            reset();
        }

    }
}

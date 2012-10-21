
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.math.optimization.OptimizationException;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 3
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */
public class Assignment3 {

    /**
     * This function runs three matches:
     * Predator Minimax versus Prey Random
     * Predator Random versus Prey Minimax
     * Predator Minimax versus Preu Minimax
     * The results are saved to an M-file
     * @throws OptimizationException 
     */
    public void miniMax() throws OptimizationException {
        int nrMatches = 3;
        int nrSweeps = 10;
        int averageOver = 25;
        Position preyPos = new Position(5, 5);
        Position predPos = new Position(0, 0);
        PredatorMiniMax predMVsR = new PredatorMiniMax(predPos, preyPos, 0.0, 0.8, 0.001);
        PredatorMiniMax predMVsM = new PredatorMiniMax(predPos, preyPos, 0.0, 0.8, 0.001);
        ArrayList<Position> others1 = new ArrayList<Position>();
        others1.add(preyPos);
        PredatorRandom predRVsM = new PredatorRandom(predPos, others1);
        ArrayList<Position> others2 = new ArrayList<Position>();
        others2.add(preyPos);
        PreyRandom preyRVsM = new PreyRandom(preyPos, others2);
        PreyMinimax preyMVsR = new PreyMinimax(preyPos, predPos, 0.0, 0.8, 0.001);
        PreyMinimax preyMVsM = new PreyMinimax(preyPos, predPos, 0.0, 0.8, 0.001);

        int[][] steps = new int[nrMatches][nrSweeps];
        double[][] maxDif = new double[nrMatches][nrSweeps];
        ArrayList<Agent> a = new ArrayList<Agent>();
        Environment env = new Environment(a, preyRVsM);

        System.out.println("Match random prey vs minimax predator");
        //Match random prey vs minimax predator        
        a.add(predMVsR);        
        View v = new View(env);
        for (int n = 0; n < averageOver; n++) {
            System.out.println("Average over nr: "+n);
            for (int s = 0; s < nrSweeps; s++) {
                int envRes = env.doRunNTL();
                steps[0][s] += env.getNrSteps();
                if (envRes != -1) {
                }
                predMVsR.learn(1);
                maxDif[0][s] += predMVsR.getLargestDiff();
            }
            if (n == 0) {
                predMVsR.printV(true);
                v.printPolicy(predMVsR, 5, 5, true);
            }
            predMVsR.forgetLearning();
        }

        System.out.println("Match random predator vs minimax prey");
        //Match random predator vs minimax prey
        a = new ArrayList<Agent>();
        a.add(predRVsM);
        env = new Environment(a, preyMVsR);
        for (int n = 0; n < averageOver; n++) {            
            System.out.println("Average over nr: "+n);
            for (int s = 0; s < nrSweeps; s++) {
                env.doRunNTL();
                steps[1][s] += env.getNrSteps();
                preyMVsR.learn(1);
                maxDif[1][s] += preyMVsR.getLargestDiff();
                env.resetNrSteps();
            }
            if (n == 0) {
                preyMVsR.printV(true);
                v.printPolicy(preyMVsR, 5, 5, false);
            }
            preyMVsR.forgetLearning();
        }

        System.out.println("Match minimax predator vs minimax prey");
        //Match minimax predator vs minimax prey
        a = new ArrayList<Agent>();
        a.add(predMVsM);
        env = new Environment(a, preyMVsM);
        for (int n = 0; n < averageOver; n++) {
            System.out.println("Average over nr: "+n);
            for (int s = 0; s < nrSweeps; s++) {
                env.doRunNTL();
                steps[2][s] += env.getNrSteps();
                predMVsM.learn(1);
                preyMVsM.learn(1);
                env.resetNrSteps();
            }
            if (n == 0) {
                preyMVsM.printV(true);
                predMVsM.printV(true);
                v.printPolicy(preyMVsM, 5, 5, false);
                v.printPolicy(predMVsM, 5, 5, true);
            }
            preyMVsM.forgetLearning();
            predMVsM.forgetLearning();
        }
        
        View.writeMinimaxResultsToMatlab(steps, "resultsMinimax.m", nrMatches, averageOver, nrSweeps);

    }


    /**
     * This function shows that the environment works properly by doing one run with the desired
     * number of agents and printing each step and the accompanying reward for prey and predator
     * @param nrPredators = nr of predators in testRun
     */
    public void testEnvironment(int nrPredators) {
        int[][] options = {{0, 0}, {2, 2}, {8, 8}, {2, 8}};
        ArrayList<Position> startPosPreds = new ArrayList<Position>();
        ArrayList<Agent> predators = new ArrayList<Agent>();
        Position preyPos = new Position(5, 5);
        for (int i = 0; i < nrPredators; i++) {
            startPosPreds.add(new Position(options[i][0], options[i][1]));
        }
        PreyRandom prey = new PreyRandom(new Position(5, 5), Position.deepCopyList(startPosPreds));
        Agent a;
        ArrayList<Position> others;
        for (int i = 0; i < nrPredators; i++) {
            others = new ArrayList<Position>();
            others.add(preyPos);
            for (int j = 0; j < nrPredators; j++) {
                if (i != j) {
                    others.add(new Position(startPosPreds.get(j)));
                }
            }
            predators.add(new PredatorRandom(startPosPreds.get(i), others));
        }
        Environment env = new Environment(predators, prey);
        View v = new View(env);
        while (!env.isEnded()) {
            env.nextTimeStep();
            System.out.print("reward prey: " + env.reward(true));
            System.out.println(" reward predator: " + env.reward(false) + "\n");
            v.printSimple();
        }
    }

    
    /**
     * Function that creates a matlab script for the plot of the averaged percentage 
     * of times the predators won per episode over the number of trials.
     * @param nrEpisodes
     * @param nrTrials
     * @param nrPredatorsSettings
     * @param print
     */
    public void processIQLwinning(int nrEpisodes, int nrTrials, double[] nrPredatorsSettings, boolean print) {

        String metric="winning";

        double episodesMatrix[][] = new double[nrPredatorsSettings.length][nrEpisodes];
        String paramName = "Nr. predators";
        String yLabel = "Percentage of times predators won";

        for (int i=0; i < nrPredatorsSettings.length; i++) {
            episodesMatrix[i] = independentQLearning( nrEpisodes,  nrTrials,  (int) nrPredatorsSettings[i],  print, metric);
        }
        View.episodeMatrixToMatlabScript("IQL_winning.m", episodesMatrix, nrPredatorsSettings, paramName, yLabel,"NorthWest");

    }

    
    /**
     * Function that creates a matlab script for the plot of the average number 
     * of timesteps per episode over a number of trials
     * @param nrEpisodes
     * @param nrTrials
     * @param nrPredatorsSettings
     * @param print
     */
    public void processIQLnrTimeSteps(int nrEpisodes, int nrTrials, double[] nrPredatorsSettings, boolean print) {

        String metric="nrTimeSteps";

        double episodesMatrix[][] = new double[nrPredatorsSettings.length][nrEpisodes];
        String paramName = "Nr. predators";
        String yLabel = "Avg. number time steps";

        for (int i=0; i < nrPredatorsSettings.length; i++) {
            episodesMatrix[i] = independentQLearning( nrEpisodes,  nrTrials,  (int) nrPredatorsSettings[i],  print,metric);
        }
        View.episodeMatrixToMatlabScript("IQL_nrTimeSteps.m", episodesMatrix, nrPredatorsSettings, paramName, yLabel,"NorthWest");

    }

    
    /**
     * Independent Q learning 
     * 
     * @param nrEpisodes
     * @param nrTrials
     * @param nrPredators
     * @param print
     * @param metric
     * @return
     */
    public double[] independentQLearning(int nrEpisodes, int nrTrials, int nrPredators, boolean print, String metric) {

        /** init parameters and positions **/
        double gamma = 0.9;
        double alpha = 0.5;
        double epsilon = 0.1;

        int[][] options = {{0, 0}, { Environment.HEIGHT-1, Environment.WIDTH-1}, {0, Environment.WIDTH-1}, { Environment.HEIGHT-1, 0}};
        Position preyPos = new Position((int) Math.floor(Environment.HEIGHT/2), (int) Math.floor(Environment.WIDTH/2));
        System.out.println(preyPos.getX());

        ArrayList<Agent> predators = new ArrayList<Agent>();
        for (int i = 0; i < nrPredators; i++) { //// gamma, double alpha, int nrOtherPredators, double maxChange, double actionSelectionParameter, Position startPos
            predators.add(new AgentQLearning(gamma, alpha, nrPredators, 0, epsilon, new Position(options[i][0], options[i][1])));
            System.out.println("made agent");

        }
        AgentQLearning prey = new AgentQLearning(gamma, alpha, nrPredators, 0, epsilon, preyPos);

        System.out.println("made agents");
        
        /** get statistics **/
        double[] metricValues = new double[nrEpisodes];

        for (int episodeNr = 0; episodeNr < nrEpisodes; episodeNr++) {
            metricValues[episodeNr] = independentQLearningTrials(predators, prey, nrTrials, nrPredators, print, metric);

          //  System.out.println("metric: " + metricValues[episodeNr]);
        }

          View.printPolicy(predators.get(0),5,5 , true);
          System.out.println("\n for prey: \n");
          View.printPolicy(prey,5,5 , false);
        return metricValues;
    }

    /**
     * Runs a trial for independent Q learning and returns the given metric value;
     * 
     * @param predators
     * @param prey
     * @param nrTrials
     * @param nrPredators
     * @param print
     * @return
     */
    public double independentQLearningTrials(ArrayList<Agent> predators, Agent prey, int nrTrials, int nrPredators, boolean print, String metric) {

        int nrTrialsPredatorsWon = 0;
        int nrTimeSteps=0;

        for (int i = 0; i < nrTrials; i++) {
            Environment env = new Environment(predators, prey);
            View v = new View(env);
            
            while (!env.isEnded()) {
                env.nextTimeStep();
                nrTimeSteps++;

                if (print) {
                    v.printSimple();
                }
            }

            if (env.reward(false) == Environment.maximumReward) { // if predators accomplished mission
                nrTrialsPredatorsWon++;
            }
            
            env.reset();
        }


        if (metric == "winning")
            return ((double) nrTrialsPredatorsWon / nrTrials) * 100;
        else  if (metric == "nrTimeSteps")
            return ((double) nrTimeSteps) / nrTrials;
        else {
           // System.out.println("ERROR: Wrong value for param metric");
            return 0;
        }
    }

    public static void main(String[] args) throws OptimizationException {
        Assignment3 a = new Assignment3();
//		a.testEnvironment(3);
//		a.miniMax();
//		a.minimaxPredVsRandomPrey(true);
//		a.independentQLearning(5, 4, 4, false,"measureNothing"); // nr episodes, nr trials (per episode), nr predators, print, metric("nrTimeSteps","winning" of niks
//		a.independentQLearning(5, 4, 4, false,"measureNothing"); // nr episodes, nr trials (per episode), nr predators, print, metric("nrTimeSteps","winning" of niks

        //state rep test
//		StateRep rep = new StateRep(10,false,3);
//		rep.test();
        
        //creating plots		
//		a.processIQLwinning(5000, 200, new double[]{2,3,4}, false);
		a.processIQLnrTimeSteps(1000, 200, new double[]{1}, false);
	
//		a.independentQLearning(2500, 25,  1, false, "winning");
    }
}

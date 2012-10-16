
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.math.optimization.OptimizationException;

/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 1
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */
public class Assignment3 {

//    /**
//     * Reads in the supposed optimal Q(s,a) function (i.e. table) from a file.
//     * Made to process a file were each staterep-traingle of doubles if precede by "Action = ..."
//     * This kind of can be generated as text using  the function printAll(false) in StateRepresentation
//     *
//     * @see StateRepresentation::printAll(..)
//     *
//     * @param filename
//     */
//    public void readBestValueFunctionFromFile(String filename) {
//        try {
//            Scanner scanner = new Scanner(new File(filename));
//
//            int stateNumber = 0;
//            int actionNumber = 0;
//
//            while (scanner.hasNext() && actionNumber < Action.nrActions) {
//
//                String notAction = scanner.next();
//                while (!notAction.equals("Action")) {
//                    notAction = scanner.next();
//                    //System.out.println(notAction);
//                }
//                scanner.nextLine(); // vb. " = Hor.Approach"
//
//                while (scanner.hasNextDouble()) {
//                    double stateActionValue = scanner.nextDouble();
//                    //System.out.println(stateActionValue + " at state " + stateNumber + " action " + actionNumber);
//
//                    bestStateRep.setValue(stateNumber, StateRepresentation.Action.actionValues[actionNumber], stateActionValue);
//                    stateNumber++;
//                }
//
//                stateNumber = 0;
//                actionNumber++;
//            }
//        }
//        catch (FileNotFoundException e) {
//            System.out.println("Error in Assignment2::readBestValueFunctionFromFile(..): File " + filename + " not found!");
//        }
//    }
//
//
//
//
//    /**
//     * Makes matlab scripts that plot statistics for Q-Learning using a given action selection method with
//     * given parameters
//     *
//     * @see QLearningCompareActionselections(..)
//     *
//     * @param epsilonGreedy :   true if action selection method should be Epsilon-greedy,
//     *                          false if action selection method should be Softmax
//     *
//     * @param values        :   The array containing the action selection parameters values you want to
//     *                          test for the given action selection method (the epsilons or taus)
//     */
//    public void QLearningMakeActionselectionPlots(boolean epsilonGreedy, double values[]) {
//        PredatorQLearning agent = new PredatorQLearning(0.9, 0.5, 0.1, 0.1, PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//        Environment env = new Environment(agent, new Position(5, 5));
//        int nrTestRuns = 100;
//        int nrEpisodes = 1000;
//
//        double optimalActionValues[][] = new double[values.length][nrEpisodes];
//        double percentageStateActionPairsVisited[][] = new double[values.length][nrEpisodes];
//        double nrStepsUsed[][] = new double[values.length][nrEpisodes];
//
//        for (int j = 0; j < values.length; j++) {
//            for (int i = 0; i < nrTestRuns; i++) {
//
//                agent = epsilonGreedy ? new PredatorQLearning(0.9, 0.5, 0.1, values[j], PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0))
//                        : new PredatorQLearning(0.9, 0.5, 0.1, values[j], PredatorQLearning.ActionSelection.softmax, new Position(0, 0));
//
//                env = new Environment(agent, new Position(5, 5));
//
//                int episode = 0;
//                do {
//                    env.doRun();
//
//                    percentageStateActionPairsVisited[j][episode] += agent.getPercentageStateActionPairsVisited() / nrTestRuns;
//                    optimalActionValues[j][episode] += percentageOptimalAction(agent) / nrTestRuns;
//                    nrStepsUsed[j][episode] += ((double) agent.getNrStepsUsed()) / nrTestRuns;
//
//                    episode++;
//
//                } while (episode < nrEpisodes);
//            }
//        }
//        String valueName = epsilonGreedy ? "epsilon" : "tau";
//        View.episodeMatrixToMatlabScript("firstShould_optimalValues_" + valueName + ".m", optimalActionValues, values, valueName, "% Optimal Action");
//        View.episodeMatrixToMatlabScript("firstShould_visitedPairs_" + valueName + ".m", percentageStateActionPairsVisited, values, valueName, "% State-Action pairs visited");
//        View.episodeMatrixToMatlabScript("firstShould_nrSteps_" + valueName + ".m", nrStepsUsed, values, valueName, "Number of steps");
//    }
//
//    /**
//     * FIRST SHOULD
//     *
//     * Compare the Q-Learning algorithm using Epsilon-greedy action selection to
//     * the case of it using Softmax action selection.
//     *
//     * Makes matlab scripts that generate plots.
//     */
//    public void QLearningCompareActionselections() {
//        boolean epsilonGreedy = true;
//        boolean softmax = !epsilonGreedy;
//
//        QLearningMakeActionselectionPlots(epsilonGreedy, new double[]{0.01, 0.1, 0.5, 1});
//        QLearningMakeActionselectionPlots(softmax, new double[]{0.1, 0.5, 1, 10});
//    }
//
//    /**
//     * Calculate the normalized root mean square error (NRMSE),
//     * averaged over state-action pairs,
//     * for the Q(s,a)-function (i.e. table) of a PredatorQLearning, compared to the optimal Q(s,a)-function (i.e. table)
//     *
//     * @see readBestValueFunctionFromFile(..), form which the optimal Q(s,a)-function should be read
//     *
//     * @param agent     :   the QLearning agent
//     */
//    public double calculateNRMSE(PredatorQLearning agent) {
//
//        double highestActionValue = Math.min(Environment.minimumReward, agent.getInitialValue());
//        double lowestActionValue = Math.max(Environment.maximumReward, agent.getInitialValue());
//
//        double RMSE = 0;
//        double numerator = 0;
//
//        for (int stateNr = 0; stateNr < StateRepresentation.nrStates; stateNr++) {
//            for (int actionNr = 0; actionNr < StateRepresentation.nrActions; actionNr++) {
//
//                double v1 = bestStateRep.getValue(stateNr, StateRepresentation.Action.actionValues[actionNr]);
//                double v2 = agent.getValueStateAction(stateNr, StateRepresentation.Action.actionValues[actionNr]);
//                double difference = v1 - v2;
//
//                numerator += Math.pow(difference, 2);
//
//                if (v1 > highestActionValue) {
//                    highestActionValue = v1;
//                }
//
//                if (v2 > highestActionValue) {
//                    highestActionValue = v2;
//                }
//
//                if (v1 < lowestActionValue) {
//                    lowestActionValue = v1;
//                }
//
//                if (v2 < lowestActionValue) {
//                    lowestActionValue = v2;
//                }
//
//            }
//        }
//        RMSE = Math.sqrt(numerator / StateRepresentation.nrStateActionPairs);
//        double NRMSE = RMSE / (highestActionValue - lowestActionValue);
//        return NRMSE;
//    }
//
//    /**
//     * Calculate the percentage of time that a PredatorQLearning agent will take an
//     * optimal action given a state, for all states.
//     * The (last) optimal action for any state is processed from to the optimal V-function.
//     *
//     * @see readBestValueFunctionFromFile(..), form which the optimal Q(s,a)-function should be read
//     *
//     * @param agent     :   the QLearning agent
//     */
//    public double percentageOptimalAction(PredatorQLearning agent) {
//
//        double nrOptimalAction = 0;
//
//        for (int stateNr = 0; stateNr < StateRepresentation.nrStates; stateNr++) {
//
//            int bestActionNr1 = -1;
//            int bestActionNr2 = -1;
//
//            double bestActionValue1 = Math.min(Environment.minimumReward, agent.getInitialValue());
//            double bestActionValue2 = bestActionValue1;
//
//            double values1[] = bestStateRep.getStateActionPairValues(stateNr);
//            double values2[] = agent.getStateActionPairValues(stateNr);
//
//            // for both statereps,
//            // just check one, last "best action"
//            for (int actionNr = 0; actionNr < StateRepresentation.nrActions; actionNr++) {
//                if (values1[actionNr] > bestActionValue1) {
//                    bestActionValue1 = values1[actionNr];
//                    bestActionNr1 = actionNr;
//                }
//                if (values2[actionNr] > bestActionValue2) {
//                    bestActionValue2 = values2[actionNr];
//                    bestActionNr2 = actionNr;
//                }
//            }
//            if (bestActionNr1 == bestActionNr2) {
//                nrOptimalAction++;
//            }
//        }
//        return (nrOptimalAction / StateRepresentation.nrStates) * 100;
//    }
//
//    /**
//     * Makes matlab scripts that plot statistics for Q-Learning for different settings
//     * of the discount factor (gamma)
//     *
//     * @see QLearningCompareAlphasAndDfs(..)
//     *
//     * @param nrTestRuns    :   number of runs for each episode (will be averaged over)
//     * @param nrEpisodes    :   number of episodes (per setting)
//     */
//    public void QLearningDfsPlots(int nrTestRuns, int nrEpisodes) {
//
//        double DFvalues[] = new double[]{0.1, 0.5, 0.7, 0.9};
//
//        double NRMSEvalues[][] = new double[DFvalues.length][nrEpisodes];
//        double optimalActionValues[][] = new double[DFvalues.length][nrEpisodes];
//
//        for (int j = 0; j < DFvalues.length; j++) {
//            for (int i = 0; i < nrTestRuns; i++) {
//
//                PredatorQLearning agent = new PredatorQLearning(DFvalues[j], 0.5, 0.1, 0.1, PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//                Environment env = new Environment(agent, new Position(5, 5));
//                int episode = 0;
//                do {
//                    env.doRun();
//
//                    NRMSEvalues[j][episode] += calculateNRMSE(agent) / nrTestRuns;
//                    optimalActionValues[j][episode] += percentageOptimalAction(agent) / nrTestRuns;
//                    episode++;
//
//                } while (episode < nrEpisodes);
//            }
//        }
//        View.episodeMatrixToMatlabScript("qLearning_DF_NRMSE.m", NRMSEvalues, DFvalues, "df", "NRMSE");
//        View.episodeMatrixToMatlabScript("qLearning_DF_POA.m", optimalActionValues, DFvalues, "df", "% Optimal Action");
//
//    }
//
//    /**
//     * Makes matlab scripts that plot statistics for Q-Learning for different settings
//     * of alpha (learning rate)
//     *
//     * @see QLearningCompareAlphasAndDfs(..)
//     *
//     * @param nrTestRuns    :   number of runs for each episode (will be averaged over)
//     * @param nrEpisodes    :   number of episodes (per setting)
//     */
//    public void QLearningAlphasPlots(int nrTestRuns, int nrEpisodes) {
//
//        double alphaValues[] = new double[]{0.1, 0.2, 0.3, 0.5, 0.7};
//
//        double NRMSEvalues[][] = new double[alphaValues.length][nrEpisodes];
//        double optimalActionValues[][] = new double[alphaValues.length][nrEpisodes];
//
//        for (int j = 0; j < alphaValues.length; j++) {
//            for (int i = 0; i < nrTestRuns; i++) {
//
//                PredatorQLearning agent = new PredatorQLearning(0.9, alphaValues[j], 0.1, 0.1, PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//                Environment env = new Environment(agent, new Position(5, 5));
//                int episode = 0;
//                do {
//                    env.doRun();
//
//                    NRMSEvalues[j][episode] += calculateNRMSE(agent) / nrTestRuns;
//                    optimalActionValues[j][episode] += percentageOptimalAction(agent) / nrTestRuns;
//                    episode++;
//
//                } while (episode < nrEpisodes);
//            }
//        }
//        View.episodeMatrixToMatlabScript("qLearning_Alpha_NRMSE.m", NRMSEvalues, alphaValues, "alpha", "NRMSE");
//        View.episodeMatrixToMatlabScript("qLearning_Alpha_POA.m", optimalActionValues, alphaValues, "alpha", "% Optimal Action");
//    }
//
//    /**
//     * FIRST MUST
//     *
//     * Investigates the performance of the Q-Learning agent, one time for different values of
//     * alpha, the learning rate (0.1, 0.2, 0.3, 0.5, 0.7) and one time for different discount factors (gamma)
//     * (0.1, 0.5, 0.7, 0.9).
//     * (Default setting for alpha is 0.5, for discount factor is 0.9).
//     *
//     */
//    public void QLearningCompareAlphasAndDfs() {
//
//        /**Params:                                      gamma, alpha, maxChange,  a.s.Parameter, actionSelectionMethod, startPosition
//         * Note the param maxChange is effectless in this case because we use a fixed number of episodes                                       **/
//        PredatorQLearning agent = new PredatorQLearning(0.9, 0.5, 0.1, 0.1, PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//
//        int nrTestRuns = 100;
//        int nrEpisodes = 1000;
//
//        QLearningDfsPlots(nrTestRuns, nrEpisodes);
//        QLearningAlphasPlots(nrTestRuns, nrEpisodes);
//
//
//    }
//
//    /**
//     * SECOND MUST
//     *
//     * Compare the Q-Learning algorithm using both
//     * different values of epsilon (using Epsilon-greedy action selection)
//     * and the value the  Q(a,s)-table is initialized with.
//     *
//     * The values chosen are a high and a low one for both parameters (i.e. the total
//     * number of combinatorial settings is 4).
//     *
//     * Makes matlab scripts that generate plots.
//     */
//    public void QLearningCompareEpsilonsAndInits() {
//
//        PredatorQLearning agent = new PredatorQLearning(0.9, 0.5, 0.1, 0.1, PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//        Environment env = new Environment(agent, new Position(5, 5));
//
//        int nrTestRuns = 100;
//        int nrEpisodes = 1000;
//
//        double initEpsilonValues[][] = new double[][]{{0, 0.1}, {0, 0.9}, {15, 0.1}, {15, 0.9}};
//
//        double optimalActionValues[][] = new double[initEpsilonValues.length][nrEpisodes];
//        double percentageStateActionPairsVisited[][] = new double[initEpsilonValues.length][nrEpisodes];
//        double nrStepsUsed[][] = new double[initEpsilonValues.length][nrEpisodes];
//
//
//        for (int j = 0; j < initEpsilonValues.length; j++) {
//            for (int i = 0; i < nrTestRuns; i++) {
//
//                agent = new PredatorQLearning(0.9, 0.5, 0.1, initEpsilonValues[j][1], PredatorQLearning.ActionSelection.epsilonGreedy, new Position(0, 0));
//                env = new Environment(agent, new Position(5, 5));
//                agent.setInitialValue(initEpsilonValues[j][0]);
//
//                int episode = 0;
//                do {
//                    env.doRun();
//
//                    percentageStateActionPairsVisited[j][episode] += agent.getPercentageStateActionPairsVisited() / nrTestRuns;
//                    optimalActionValues[j][episode] += percentageOptimalAction(agent) / nrTestRuns;
//                    nrStepsUsed[j][episode] += ((double) agent.getNrStepsUsed()) / nrTestRuns;
//
//                    episode++;
//
//                } while (episode < nrEpisodes);
//            }
//        }
//        View.episodeMatrixToMatlabScript2D("qLearning_optimalValues.m", optimalActionValues, initEpsilonValues, "init.val.", "epsilon", "% Optimal Action");
//        View.episodeMatrixToMatlabScript2D("qLearning_visitedPairs.m", percentageStateActionPairsVisited, initEpsilonValues, "init.val.", "epsilon", "% State-Action pairs visited");
//        View.episodeMatrixToMatlabScript2D("qLearning_nrSteps.m", nrStepsUsed, initEpsilonValues, "init.val.", "epsilon", "Number of steps");
//    }
//
    /**
     * This function shows that the environment works properly by doing one run with the desired
     * number of agents and printing each step and the accompanying reward for prey and predator
     * @param nrPredators = nr of predators in testRun
     */
    public void testEnvironment(int nrPredators ){
        int [][] options = {{0,0},{2,2},{8,8},{2,8}};
        ArrayList<Position> startPosPreds = new ArrayList<>();
        ArrayList<Agent> predators = new ArrayList<>();
        Position preyPos = new Position(5,5);
        for(int i = 0; i<nrPredators;i++){
            startPosPreds.add(new Position(options[i][0],options[i][1]));
        }
        PreyRandom prey = new PreyRandom(new Position(5,5), Position.deepCopyList(startPosPreds), new StateRep(15, true, nrPredators), 5);
        Agent a;
        ArrayList<Position> others;
        for(int i = 0; i<nrPredators;i++){
            others = new ArrayList<>();
            others.add(preyPos);
            for(int j = 0; j<nrPredators;j++){
                if(i!=j){
                    others.add(new Position(startPosPreds.get(j)));
                }
            }
            StateRep s = new StateRep(15, false, nrPredators);
            predators.add(new PredatorRandom(startPosPreds.get(i), others, s ,5));
        }
        Environment env = new Environment(predators, prey);
        View v = new View(env);
        while(!env.isEnded()){
            env.nextTimeStep();
            System.out.print("reward prey: "+env.reward(true));
            System.out.println(" reward predator: "+env.reward(false)+"\n");
            v.printSimple();
        }
    }
    
    public void miniMax() throws OptimizationException{
        PredatorMiniMax pred = new PredatorMiniMax(new Position(0,0), new Position(5,5), 15.0, 0.8);
        pred.learn();
    }

    public static void main(String[] args) throws OptimizationException {
        Assignment3 a = new Assignment3();
//        a.testEnvironment(3);
        a.miniMax();

//        StateRep rep = new StateRep(10,false,3);
//        rep.test();

    }
}

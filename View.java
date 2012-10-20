
/**
 * Master AI UvA 2012/2013
 * Autonomous Agents
 * Assignment 1
 *
 * @authors Group 7: Agnes van Belle, Maaike Fleuren, Norbert Heijne, Lydia Mennes
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class View {

    private final static String EMPTY = "_";
    private final static String PREY = "b";
    private final static String PREDATOR = "P";
    private Environment env;
    String[][] world;

    public View(Environment env) {
        this.env = env;
    }

//    /**
//     * Print the gridworld as a gridworld
//     */
//    public void print() {
//        world[env.getPreyPos().getY()][env.getPreyPos().getX()] = PREY;
//        world[env.getPredatorPos().getY()][env.getPredatorPos().getX()] = PREDATOR;
//
//        for (String[] row : world) {
//            for (String place : row) {
//                System.out.print(place + "  ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//
//        world[env.getPredatorPos().getY()][env.getPredatorPos().getX()] = EMPTY;
//        world[env.getPreyPos().getY()][env.getPreyPos().getX()] = EMPTY;
//    }

    /*
     * Prints the real world "grid world" including the prey and predator in it
     */
    public void printSimple(){
        System.out.print("  ");
        for(int j = 0; j<Environment.WIDTH;j++){
            System.out.print(j+" ");
        }
        System.out.print("\n");
        for(int i =0;i<Environment.HEIGHT;i++){
            System.out.print(String.format("%2d ",i));
            for(int j = 0; j<Environment.WIDTH;j++){               
                
                if(env.predatorStandsHere(new Position(j,i))){
                    System.out.print(PREDATOR);
                }
                else if(env.getPreyPos().equals(new Position(j,i))){
                    System.out.print(PREY);
                }
                else{
                    System.out.print(EMPTY);
                }
                System.out.print(" ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

//    private void fill() {
//        for (String[] row : world) {
//            Arrays.fill(row, EMPTY);
//        }
//    }

    /**
     * Write a matlab script that plots a colormap of 2D VMatrix to a file
     * @param filename : filename of that script
     */
    public static void writeVMatrix(String filename, double[][] VMatrix) {
        try {
            FileWriter fstream = new FileWriter(filename, false);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write("clear;clc;");
            out.newLine();
            for (int i = 0; i < VMatrix.length; i++) {
                out.write(String.format("C(%d,:)=[", i + 1));
                for (int j = 0; j < VMatrix[i].length; j++) {
                    if (j != 0) {
                        out.write(",");
                    }

                    String number;
                    number = String.format("%.3f", VMatrix[i][j]);
                    out.write(number.replaceAll(",", "."));
                }
                out.write("];");
                out.newLine();
                out.flush();
            }
            out.write("imagesc(C, [ min(min(C)), max(max(C)) ] );");
            out.write("colormap(gray);");
            out.write("axis image");
            out.write("axis off ");
            out.flush();
            fstream.close();
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error in writeVMatrix(): " + e);
        }
    }


     /**
     * Print the policy of a given agent in a grid-world view
     * where the prey is FIXED on one position in that grid-world
     *
     * @param agent
     * @param xPrey : x position of prey in real world
     * @param yPrey : y position of prey in real world
     */
    public void printPolicy(Agent agent, int xPrey, int yPrey, boolean forPred) {
        System.out.println(" \\begin{table}[htbp]\n"
                + "\\caption{policy}\n"
                + "\\label{policyLabel}\n"
                + "\\centering\n"
                + "\\begin{footnotesize}\n"
                + "\\begin{tabular}{c|c|c|c|c|c|c|c|c|c|c|c|}\n"
                + "&0&1&2&3&4&5&6&7&8&9&10\\\\ \\hline\\\\");
        Position prey = new Position(xPrey, yPrey);
        double[][] probabilities = new double[Environment.WIDTH][Action.nrActions];
        //For each y-coordinate
        for(int row = 0; row <Environment.HEIGHT;row++){
            //for each x-coordinate
           for(int col = 0; col <Environment.WIDTH;col++){
                //If it is not the goal state
                if(xPrey != col || yPrey!=row){
                    //Get the probabilities for real world actions
                    double [] prob = agent.policy(prey, new Position(col,row), new ArrayList<Position>());
                    for(int i = 0; i<Action.nrActions;i++){
                        probabilities[col][i]=prob[i];
                    }
             }
            }
            String [] directions = {"U", "R", "D", "L","W"};
            //Print probabilities for each action
            for(int i = 0; i<Action.nrActions;i++){
                    if(i==0){
                        System.out.print(row + "&");
                    }
                    else{
                       System.out.print("&");
                    }
                    for(int col = 0; col<Environment.WIDTH;col++){
                        if(xPrey != col || yPrey!=row){
                            System.out.print(directions[i] + " " + String.format("%.3f",probabilities[col][i]));
                        }
                        else if(i == 2){
                            if(forPred){
                                System.out.print("Prey");
                            }
                            else{
                                System.out.print("Pred");
                            }                            
                        }
                        if(col == Environment.WIDTH-1){
                            System.out.println("\\\\");
                        }
                        else{
                            System.out.print("&");
                        }
                    }
            }
            System.out.println("\\hline \\\\");

        }
        System.out.print("\\end{tabular}\n"+
                        "\\end{footnotesize}\n"+
                       "\\end{table}\n");
    }


    /**
     * Writes a Matlab script that plots performance measures given different parameter
     * settings for one parameter.
     *
     * @param episodesMatrix    :   In episodesMatrix, rows denote the parameter's values.
     *                              The columns should denote the episodes, i.e. column 1  = episode 1.
     *                              The content is the value of the performance measure.
     * @param paramValues       :   Values that the parameter had, for the rows of episodesMatrix.
     *                              For example, {0.1, 0.2, 0.5, 0.7}
     *
     * @see Assignment2, QLearning functions
     */
    public static void episodeMatrixToMatlabScript(String filename, double episodesMatrix[][], double paramValues[], String paramName, String yLabel,String legendLocation) {
        int nrEpisodes = episodesMatrix[0].length;
        int nrParamValues = episodesMatrix.length;

        try {
            FileWriter fstream = new FileWriter(filename, false);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write("clear;clc;");
            out.newLine();
            out.write("nrEpisodes = " + nrEpisodes + ";");
            out.newLine();

            for (int j=0; j < nrParamValues; j++) {
                for (int i = 0; i < nrEpisodes; i++) {

                    String number;
                    number = String.format("%.4f", episodesMatrix[j][i]);

                    out.write("E" + j + "(" + (i + 1) + ",1)=");
                    out.write(number.replaceAll(",", "."));
                    out.write(";");
                    out.newLine();
                }
                out.newLine();
                out.flush();
            }
            out.write("figure('visible','on'); xAxis = linspace(1,nrEpisodes,nrEpisodes);");
            out.newLine();

            StringBuilder plotText = new StringBuilder();
            plotText.append("plot(");
            for (int j=0; j < nrParamValues; j++) {
                if (j != 0 )
                     plotText.append(",");
                plotText.append("xAxis, E").append(j);
            }
            plotText.append(", 'LineWidth',1.5");
            plotText.append(");");

            out.write(plotText.toString());
            out.newLine();

            out.write("title('Agent performance');"
                        + "xlabel('Episode');"
                        + "ylabel('" + yLabel + "');");
            out.newLine();

            StringBuilder legend = new StringBuilder();
            legend.append("legend(");
            for (int j=0; j < nrParamValues; j++) {
                if (j != 0 )
                     legend.append(",");
                legend.append("'" + paramName + " = " + paramValues[j] + "'");
            }
            legend.append(", 'Location','"+legendLocation+"');");
            out.write(legend.toString());

            out.flush();
            fstream.close();
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error in episodeMatrixToMatlabScript(..): " + e);
        }
    }

    /**
     * Writes a Matlab script that plots performance measures given different parameter settings
     * for a combination of TWO parameters.
     *
     * @param episodesMatrix    :   In episodesMatrix, rows denote the combinatorial parameters configuration.
     *                              The columns should denote the episodes, i.e. column 1  = episode 1.
     *                              The content is the value of the performance measure.
     * @param paramValues       :   Combinatorial values that the parameters had, for the rows of episodesMatrix.
     *                              For example, {{0.1,10}, {0.2, 5}, {0.5, 0}, {0.7, -10}}
     *
     * @see Assignment2, QLearning functions
     */
    public static void episodeMatrixToMatlabScript2D(String filename, double episodesMatrix[][], double paramValues[][], String paramName1, String paramName2, String yLabel) {
        int nrEpisodes = episodesMatrix[0].length;
        int nrParamValues = episodesMatrix.length;

        try {
            FileWriter fstream = new FileWriter(filename, false);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write("clear;clc;");
            out.newLine();
            out.write("nrEpisodes = " + nrEpisodes + ";");
            out.newLine();

            for (int j=0; j < nrParamValues; j++) {
                for (int i = 0; i < nrEpisodes; i++) {
                    String number;
                    number = String.format("%.4f", episodesMatrix[j][i]);

                    out.write("E" + j + "(" + (i + 1) + ",1)=");
                    out.write(number.replaceAll(",", "."));

                    out.write(";");
                    out.newLine();
                }
                out.newLine();
                out.flush();
            }

            out.write("figure('visible','on'); "
                    + "xAxis = linspace(1,nrEpisodes,nrEpisodes);");

            out.newLine();

            StringBuilder plotText = new StringBuilder();
            plotText.append("plot(");
            for (int j=0; j < nrParamValues; j++) {
                if (j != 0 )
                     plotText.append(",");
                plotText.append("xAxis, E").append(j);
            }
            plotText.append(", 'LineWidth',1.5");
            plotText.append(");");

            out.write(plotText.toString());
            out.newLine();

            out.write("title('Agent performance');"
                        + "xlabel('Episode');"
                        + "ylabel('" + yLabel + "');");
            out.newLine();

            StringBuilder legend = new StringBuilder();
            legend.append("legend(");
            for (int j=0; j < nrParamValues; j++) {
                if (j != 0 )
                     legend.append(",");
                legend.append("'" + paramName1 + "=" + paramValues[j][0] + ", " + paramName2 + "=" + paramValues[j][1] + "'");
            }
            legend.append(");");
            out.write(legend.toString());

            out.flush();
            fstream.close();
            out.close();
        }
        catch (IOException e) {
            System.out.println("Error in episodeMatrixToMatlabScript(..): " + e);
        }
    }

}

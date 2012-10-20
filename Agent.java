
import java.util.ArrayList;


public interface Agent {

	
	
	public void doMove(ArrayList<Position> others);
	
	public Position getPos();
	
	public void reset();
	
	public boolean isConverged();
	
        /*If this function is called for by the prey others is the position of
         the other predators, if it is a predator the agent at position 0 is the prey*/
	public void observeReward(double reward, ArrayList<Position> others);

    public double[] policy(Position prey,Position predatorItself);

}

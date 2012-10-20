import java.util.ArrayList;

/**
 * For independent Q-Learning
 */

public class AgentQLearning implements Agent {

	private double gamma;
	private double alpha;
	private double actionSelectionParameter;
	
	private Position myPos;
	private Position startPos;
	private StateRep stateSpace;
	private double initialValue = 15;
	
	private Position oldPos;
	private ArrayList<Position> oldPosOthers;
	
	private final double doubleComparisonEpsilon = 0.000001;
	private Action oldAction;
	private double currentReward;
	private int oldActionNumber;

	
	private int nrOtherPredators;

	private double maxChange;
	private double largestChange;
	private double oldLargestChange;

	/**
	 * Constructor
	 * 
	 * @param gamma
	 *            : discount factor
	 * @param alpha
	 *            : learning rate
	 * @param maxChange
	 *            : this is for when you want to invok the agent until its
	 *            maximal change in all Q(s,a) values is maxChange
	 * @param actionSelectionParameter
	 *            : epsilon or tau, for any of the two action selection method
	 * @param a
	 *            : action selection method used, Epsilon-greedy or Softmax
	 * @param startPos
	 */
	public AgentQLearning(double gamma, double alpha, int nrOtherPredators, double maxChange, double actionSelectionParameter, Position startPos) {
		this.gamma = gamma;
		this.alpha = alpha;
		this.actionSelectionParameter = actionSelectionParameter;
		this.maxChange = maxChange;
		
		this.startPos = new Position(startPos);
		this.myPos = new Position(startPos);
		
		this.nrOtherPredators = nrOtherPredators;
		stateSpace = new StateRep(initialValue, nrOtherPredators);
	
	}

	/**
	 * Calculate value you can get by taking the best action from current state
	 * *
	 * 
	 * @return max_{a'} Q(s', a'), myPos should be s' when invoked
	 * 
	 */
	private double getBestActionValue(ArrayList<Position> others) {


		double[] actionvalues = stateSpace.getAllActionValues(myPos, others) ; 
		
		double bestActionValue = Environment.minimumReward;

		// get best action value
		for (int i = 0; i < Action.nrActions; i++) {

			if (actionvalues[i] > bestActionValue) {
				bestActionValue = actionvalues[i];
			}
		}
		return bestActionValue;
	
	}

	/**
	 * Is being invoked from Environment
	 * 
	 * @see Environment.nextTimeStep();
	 * 
	 *      myPos should already be adjusted
	 * @see this.doMove(...)
	 * 
	 *      observe newState & reward, update oldstate with newState state
	 *      action pair values
	 * 
	 * @param reward
	 *            : reward gotten in currentState
	 * @param other
	 *            : position of prey
	 */
	public void observeReward(double reward, ArrayList<Position> others) {
       
		currentReward = reward;

		double oldQValue = stateSpace.getActionValue(oldPos, oldPosOthers, oldAction) ;
		double maxActionValue =  getBestActionValue(others);

		double TDvalue = 	alpha *	(currentReward + (gamma * maxActionValue) - oldQValue);

		double newQValue = oldQValue + TDvalue;
		
		//System.out.println("QValue from " + oldQValue + "  to " + newQValue);

		/** Update Q(s,a) value **/
		stateSpace.setActionValue(oldPos, oldPosOthers, oldAction, newQValue);
	}

	
	
	/**
	 * Pick an action with Epsilon-greedy action selection considering the
	 * prey's location
	 * 
	 * @remark remembers old state and position
	 * 
	 * @param other
	 *            : position of the prey
	 * @return : a StateRepresentation.Action
	 */
	public Action pickEpsilonGreedyAction(ArrayList<Position> others) {

		Action action;
		
		// !!!! remember oldState
		oldPos = new Position(myPos);	
		oldPosOthers = others;

		// epsilon greedy
		if (Math.random() <= actionSelectionParameter) {
			// falls within epsilon - return uniformly random action
			action = Action.actionValues[(int) (Math.random() * Direction.nrMoves)];
		} else {
			// falls outside epsilon
			ArrayList<Action> bestActions = new ArrayList<Action>();

			double[] actionvalues = stateSpace.getAllActionValues(myPos, others);
			double bestActionValue = Math.min(Environment.minimumReward, initialValue);

			// get all greedy actions
			for (int i = 0; i < Action.nrActions; i++) {

				if (actionvalues[i] > bestActionValue) {
					bestActionValue = actionvalues[i];
					bestActions.clear();
					bestActions.add(Action.actionValues[i]);
				} 
				else if (Math.abs(actionvalues[i] - bestActionValue) < doubleComparisonEpsilon) {
					bestActions.add(Action.actionValues[i]);
				}
			}
			
            if (! bestActions.isEmpty()) // take random greedy action from the greedy actions.
                action = bestActions.get((int) (Math.random() * bestActions.size()));
            else    // all actions sucked (were not higher than Math.min(Environment.minimumReward, initialValue))
                action =  Action.actionValues[(int) (Math.random() * Direction.nrMoves)];
		}

		return action;
		
	}

	/**
	 * Pick an action (StateRepresentation.Action), convert this to a real-world
	 * action (up/right/down/left/wait) and adjust (real-world) position
	 * (myPos).
	 * 
	 * @see StateRepresentation.getMove(..)
	 * 
	 * @param other
	 *            : position of the prey
	 */
	@Override
	public void doMove(ArrayList<Position> others) {

		oldAction = pickEpsilonGreedyAction(others); // HA, HR, VA, etc.

		oldActionNumber = stateSpace.getMove(myPos, others, oldAction);

		myPos.adjustPosition(oldActionNumber);
	
	}

	@Override
	public Position getPos() {
		return myPos;
	}

	@Override
	public void reset() {
		oldLargestChange = largestChange;
		largestChange = 0;
		myPos = new Position(startPos);
	}

	public void setMaxChange(double m) {
		maxChange = m;
	}

	public double getOldLargestChange() {
		return oldLargestChange;
	}

	@Override
	public boolean isConverged() {
		return oldLargestChange <= maxChange;
		
	}

	


	public double getInitialValue() {
		return initialValue;
	}

	public void setActionSelectionParameter(double as) {
		actionSelectionParameter = as;
	}

	public double getActionSelectionParameter() {
		return actionSelectionParameter;
	}






	public void printQValues(boolean latex, int action) {
		// TODO
//		if (action == -1) {
//			stateSpace.printAll(latex);
//		} else {
//			stateSpace.printForOneAction(latex, action);
//		}
	}



	@Override
	public double[] policy(Position me,Position other ) {
		
        ArrayList<Position> allOthers = new ArrayList<Position> ();
        allOthers.add(other);
        double[] pSRActions =   stateSpace.getAllActionValues(me, allOthers);// HR, HA etc.
        double[] pActions = new double[Action.nrActions];

        for (int i=0; i < Action.nrActions; i++) {
            int index = stateSpace.getMove(myPos, allOthers, oldAction); // real world action
            pActions[index] = pSRActions[i];
        }
        return pActions;

	}

}

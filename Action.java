 /**
 * Enumerator listing all possible actions in the state space
 */
public enum Action {

    HorizontalApproach(0),
    HorizontalRetreat(1),
    VerticalApproach(2),
    VerticalRetreat(3),
    Wait(4);

    public  final static Action[]  actionValues = Action.values();
    public final static int nrActions=5;

    public final static String actionNames[] = {String.format("%-15s","Hor.Approach"),
                                                String.format("%-15s","Hor.Retreat"),
                                                String.format("%-15s","Ver.Approach"),
                                                String.format("%-15s","Ver.Retreat"),
                                                String.format("%-15s","Wait")};

    private int i;
    Action(int index){
        i = index;
    }

    int getIntValue(){
        return i;
    }
}
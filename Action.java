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
    public final static double nrActionsDouble=5.0;

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
    
    public static Action getAction(int i){
        switch(i){
            case 0: return HorizontalApproach;
            case 1: return HorizontalRetreat;
            case 2: return VerticalApproach;
            case 3: return VerticalRetreat;
            case 4: return Wait;
        }
        return null;
    }
    
    
    public static Action getReverseAction(int i){
        switch(i){
            case 0: return HorizontalRetreat;
            case 1: return HorizontalApproach;
            case 2: return VerticalRetreat;
            case 3: return VerticalApproach;
            case 4: return Wait;
        }
        return null;
    }
    
    
}
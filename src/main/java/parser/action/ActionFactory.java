package parser.action;

public final class ActionFactory {
    private ActionFactory() {}

    public static Action createAction(String actionType) {
        if ("acc".equals(actionType)) {
            return new AcceptAction();
        } else if (actionType.startsWith("r")) {
            int ruleNumber = Integer.parseInt(actionType.substring(1));
            return new ReduceAction(ruleNumber);
        } else {
            int state = Integer.parseInt(actionType.substring(1));
            return new ShiftAction(state);
        }
    }
}

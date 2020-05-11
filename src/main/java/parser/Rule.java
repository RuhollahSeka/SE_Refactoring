package parser;

/**
 * Created by mohammad hosein on 6/25/2015.
 */
public class Rule {
    private NonTerminal LHS;
    private int RHSLength = 0;
    private int semanticAction = 0;


    public Rule(String stringRule) {
        int index = stringRule.indexOf("#");
        if (index != -1) {
            try {
                this.semanticAction = Integer.parseInt(stringRule.substring(index + 1));
            } catch (NumberFormatException ignored) {

            }
            stringRule = stringRule.substring(0, index);
        }

        String[] splited = stringRule.split("->");
        this.LHS = NonTerminal.valueOf(splited[0]);
        initRHS(splited);
    }

    private void initRHS(String[] splitRules) {
        if (splitRules.length <= 1) {
            return;
        }

        String[] RHSs = splitRules[1].split(" ");
        this.RHSLength = RHSs.length;
    }

    public NonTerminal getLHS() {
        return LHS;
    }

    public int getSemanticAction() {
        return semanticAction;
    }

    public int getRHSLength() {
        return this.RHSLength;
    }
}

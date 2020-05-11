package parser.action;

import codegenerator.CodeGenerator;
import log.Log;
import parser.ParseTable;
import parser.Parser;
import parser.Rule;
import scanner.token.Token;

public class ReduceAction extends Action {
    private int ruleNumber;

    public ReduceAction(int ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    @Override
    public void takeAction(Parser parser, Token lookahead) {
        Rule rule = parser.getRule(this.ruleNumber);
        for (int i = 0; i < rule.getRHSLength(); i++) {
            parser.popParseStack();
        }

        ParseTable parseTable = parser.getParseTable();
        int parseStackTop = parser.peekParseStack();

        Log.print(parseStackTop + "\t" + rule.getLHS());
        parser.pushToStack(parseTable.getGotoTable(parseStackTop, rule.getLHS()));
        Log.print(parseStackTop + "");

        CodeGenerator codeGenerator = parser.getCodeGenerator();
        codeGenerator.semanticFunction(rule.getSemanticAction(), lookahead);
    }

    @Override
    public String toString() {
        return "r" + this.ruleNumber;
    }
}

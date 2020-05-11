package parser.action;

import parser.Parser;
import scanner.LexicalAnalyzer;
import scanner.token.Token;

public class ShiftAction extends Action {
    private int state;

    public ShiftAction(int state) {
        this.state = state;
    }

    @Override
    public void takeAction(Parser parser, Token lookahead) {
        parser.pushToStack(this.state);
    }

    @Override
    public Token getNextLookahead(LexicalAnalyzer lexicalAnalyzer, Token currentLookahead) {
        return lexicalAnalyzer.getNextToken();
    }

    @Override
    public String toString() {
        return "s" + this.state;
    }
}

package parser.action;

import parser.Parser;
import scanner.Token;

public class AcceptAction extends Action {
    @Override
    public void takeAction(Parser parser, Token lookahead) {}

    @Override
    public String toString() {
        return "acc";
    }
}

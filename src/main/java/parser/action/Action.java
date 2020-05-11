package parser.action;

import parser.Parser;
import scanner.LexicalAnalyzer;
import scanner.token.Token;

public abstract class Action {
    public abstract void takeAction(Parser parser, Token lookahead);

    public Token getNextLookahead(LexicalAnalyzer lexicalAnalyzer, Token currentLookahead) {
        return currentLookahead;
    }
}

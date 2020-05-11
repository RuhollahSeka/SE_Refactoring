package parser;


import codegenerator.CodeGenerator;
import errorhandler.ErrorHandler;
import parser.action.AcceptAction;
import parser.action.Action;
import scanner.LexicalAnalyzer;
import scanner.token.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Parser {
    private List<Rule> rules = new ArrayList<>();
    private Stack<Integer> parsStack = new Stack<>();
    private ParseTable parseTable;
    private LexicalAnalyzer lexicalAnalyzer;
    private CodeGenerator codeGenerator = new CodeGenerator();

    public Parser() {
        parsStack.push(0);
        try {
            String rawParseTable = Files.readAllLines(Paths.get("src/main/resources/parseTable")).get(0);
            parseTable = new ParseTable(rawParseTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (String stringRule : Files.readAllLines(Paths.get("src/main/resources/Rules"))) {
                rules.add(new Rule(stringRule));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startParse(java.util.Scanner sc) {
        lexicalAnalyzer = new LexicalAnalyzer(sc);
        Token lookAhead = lexicalAnalyzer.getNextToken();
        Action currentAction = null;
        while (!(currentAction instanceof AcceptAction)) {
            try {
                currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);
                currentAction.takeAction(this, lookAhead);
                lookAhead = currentAction.getNextLookahead(this.lexicalAnalyzer, lookAhead);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!ErrorHandler.hasError) {
            codeGenerator.printMemory();
        }
    }

    public void pushToStack(int state) {
        this.parsStack.push(state);
    }

    public void popParseStack() {
        this.parsStack.pop();
    }

    public int peekParseStack() {
        return this.parsStack.peek();
    }

    public Rule getRule(int ruleIndex) {
        return this.rules.get(ruleIndex);
    }

    public CodeGenerator getCodeGenerator() {
        return codeGenerator;
    }

    public ParseTable getParseTable() {
        return parseTable;
    }
}

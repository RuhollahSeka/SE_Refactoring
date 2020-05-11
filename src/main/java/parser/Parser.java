package parser;


import log.Log;
import codegenerator.CodeGenerator;
import errorhandler.ErrorHandler;
import scanner.lexicalAnalyzer;
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
    private lexicalAnalyzer lexicalAnalyzer;
    private CodeGenerator cg = new CodeGenerator();

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
        lexicalAnalyzer = new lexicalAnalyzer(sc);
        Token lookAhead = lexicalAnalyzer.getNextToken();
        boolean finish = false;
        Action currentAction;
        while (!finish) {
            try {
                Log.print(lookAhead.toString() + "\t" + parsStack.peek());
                currentAction = parseTable.getActionTable(parsStack.peek(), lookAhead);
                Log.print(currentAction.toString());

                switch (currentAction.action) {
                    case shift:
                        parsStack.push(currentAction.number);
                        lookAhead = lexicalAnalyzer.getNextToken();

                        break;
                    case reduce:
                        Rule rule = rules.get(currentAction.number);
                        for (int i = 0; i < rule.getRHSLength(); i++) {
                            parsStack.pop();
                        }

                        Log.print(parsStack.peek() + "\t" + rule.getLHS());
                        parsStack.push(parseTable.getGotoTable(parsStack.peek(), rule.getLHS()));
                        Log.print(parsStack.peek() + "");

                        try {
                            cg.semanticFunction(rule.getSemanticAction(), lookAhead);
                        } catch (Exception e) {
                            Log.print("Code Genetator Error");
                        }
                        break;
                    case accept:
                        finish = true;
                        break;
                }
                Log.print("");

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        if (!ErrorHandler.hasError)
            cg.printMemory();


    }

    public void pushToStack(int state) {
        this.parsStack.push(state);
    }

    public Rule getRule(int ruleIndex) {
        return this.rules.get(ruleIndex);
    }
}

package parser;

import parser.action.Action;
import parser.action.ActionFactory;
import scanner.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohammad hosein on 6/25/2015.
 */
public class ParseTable {
    private List<Map<Token, Action>> actionTables = new ArrayList<>();
    private List<Map<NonTerminal, Integer>> gotoTables = new ArrayList<>();

    private Map<Integer, Token> terminals = new HashMap<>();
    private Map<Integer, NonTerminal> nonTerminals = new HashMap<>();

    public ParseTable(String jsonTable) throws Exception {
        jsonTable = jsonTable.substring(2, jsonTable.length() - 2);
        String[] rows = jsonTable.split("],\\[");

        String firstRow = rows[0].substring(1, rows[0].length() - 1);
        extractSymbols(firstRow);

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i].substring(1, rows[i].length() - 1);
            updateTables(row);
        }
    }

    private void updateTables(String row) throws Exception {
        String[] columns = row.split("\",\"");

        actionTables.add(new HashMap<>());
        gotoTables.add(new HashMap<>());

        for (int j = 1; j < columns.length; j++) {
            String column = columns[j];
            if ("".equals(column)) {
                continue;
            }

            if ("acc".equals(column) || terminals.containsKey(j)) {
                Map<Token, Action> actionsTable = actionTables.get(actionTables.size() - 1);

                Token token = terminals.get(j);
                Action action = ActionFactory.createAction(column);
                actionsTable.put(token, action);
            } else if (nonTerminals.containsKey(j)) {
                Map<NonTerminal, Integer> gotoTable = gotoTables.get(gotoTables.size() - 1);
                NonTerminal nonTerminal = nonTerminals.get(j);
                int number = Integer.parseInt(column);

                gotoTable.put(nonTerminal, number);
            } else {
                throw new Exception();
            }
        }
    }

    private void extractSymbols(String firstRow) {
        String[] columns = firstRow.split("\",\"");

        for (int i = 1; i < columns.length; i++) {
            String column = columns[i];

            if (column.startsWith("Goto")) {
                String temp = columns[i].substring(5);
                try {
                    nonTerminals.put(i, NonTerminal.valueOf(temp));
                } catch (Exception ignored) {}
            } else {
                Token token = new Token(column);
                terminals.put(i, token);
            }
        }
    }

    public int getGotoTable(int currentState, NonTerminal variable) {
        return gotoTables.get(currentState).get(variable);
    }

    public Action getActionTable(int currentState, Token terminal) {
        return actionTables.get(currentState).get(terminal);
    }

}

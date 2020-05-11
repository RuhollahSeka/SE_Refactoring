package parser;

import scanner.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mohammad hosein on 6/25/2015.
 */
public class ParseTable {
    private List<Map<Token, Action>> actionTable = new ArrayList<>();
    private List<Map<NonTerminal, Integer>> gotoTable = new ArrayList<>();

    private Map<Integer, Token> terminals = new HashMap<>();
    private Map<Integer, NonTerminal> nonTerminals = new HashMap<>();

    public ParseTable(String jsonTable) throws Exception {
        jsonTable = jsonTable.substring(2, jsonTable.length() - 2);
        String[] rows = jsonTable.split("],\\[");

        String firstRow = rows[0].substring(1, rows[0].length() - 1);
        extractSymbols(firstRow);

        for (int i = 1; i < rows.length; i++) {
            rows[i] = rows[i].substring(1, rows[i].length() - 1);
            String[] cols = rows[i].split("\",\"");
            actionTable.add(new HashMap<>());
            gotoTable.add(new HashMap<>());
            for (int j = 1; j < cols.length; j++) {
                if (cols[j].equals("")) {
                    continue;
                }

                if (cols[j].equals("acc")) {
                    actionTable.get(actionTable.size() - 1).put(terminals.get(j), new Action(act.accept, 0));
                } else if (terminals.containsKey(j)) {
                    Token t = terminals.get(j);
                    Action a = new Action(cols[j].charAt(0) == 'r' ? act.reduce : act.shift, Integer.parseInt(cols[j].substring(1)));
                    actionTable.get(actionTable.size() - 1).put(t, a);
                } else if (nonTerminals.containsKey(j)) {
                    gotoTable.get(gotoTable.size() - 1).put(nonTerminals.get(j), Integer.parseInt(cols[j]));
                } else {
                    throw new Exception();
                }
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
        return gotoTable.get(currentState).get(variable);
    }

    public Action getActionTable(int currentState, Token terminal) {
        return actionTable.get(currentState).get(terminal);
    }

}

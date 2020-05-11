package codegenerator;

import errorhandler.ErrorUtils;
import scanner.Token;
import semantic.symbol.Symbol;
import semantic.symbol.SymbolTable;
import semantic.symbol.SymbolType;

import java.util.Stack;

/**
 * Created by Alireza on 6/27/2015.
 */
public class CodeGenerator {
    private Memory memory = new Memory();
    private Stack<Address> ss = new Stack<>();
    private Stack<String> symbolStack = new Stack<>();
    private Stack<String> callStack = new Stack<>();
    private SymbolTable symbolTable;

    public CodeGenerator() {
        symbolTable = new SymbolTable(memory);
    }

    public void printMemory() {
        memory.pintCodeBlock();
    }

    public void semanticFunction(int functionId, Token next) {
        switch (functionId) {
            case 0:
                return;
            case 1:
                checkID();
                break;
            case 2:
                pid(next);
                break;
            case 3:
                fpid();
                break;
            case 4:
                kpid(next);
                break;
            case 5:
                intpid(next);
                break;
            case 6:
                startCall();
                break;
            case 7:
                call();
                break;
            case 8:
                arg();
                break;
            case 9:
                assign();
                break;
            case 10:
                add();
                break;
            case 11:
                sub();
                break;
            case 12:
                mult();
                break;
            case 13:
                label();
                break;
            case 14:
                save();
                break;
            case 15:
                whileLoop();
                break;
            case 16:
                saveJpf();
                break;
            case 17:
                jpHere();
                break;
            case 18:
                print();
                break;
            case 19:
                equal();
                break;
            case 20:
                lessThan();
                break;
            case 21:
                and();
                break;
            case 22:
                not();
                break;
            case 23:
                defClass();
                break;
            case 24:
                defMethod();
                break;
            case 25:
                popClass();
                break;
            case 26:
                extend();
                break;
            case 27:
                defField();
                break;
            case 28:
                defVar();
                break;
            case 29:
                methodReturn();
                break;
            case 30:
                defParam();
                break;
            case 31:
                lastTypeBool();
                break;
            case 32:
                lastTypeInt();
                break;
            case 33:
                defMain();
                break;
        }
    }

    private void defMain() {
        memory.addThreeAddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), VarType.ADDRESS), null, null);
        String methodName = "main";
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    private void checkID() {
        symbolStack.pop();
    }

    private void pid(Token next) {
        if (symbolStack.size() > 1) {
            String methodName = symbolStack.pop();
            String className = symbolStack.pop();
            try {
                Symbol symbol = symbolTable.get(className, methodName, next.value);
                VarType t = symbol.type == SymbolType.Bool ? VarType.BOOL : VarType.INT;
                ss.push(new Address(symbol.address, t));
            } catch (Exception e) {
                ss.push(new Address(0, VarType.NON));
            }
            symbolStack.push(className);
            symbolStack.push(methodName);
        } else {
            ss.push(new Address(0, VarType.NON));
        }
        symbolStack.push(next.value);
    }

    private void fpid() {
        ss.pop();
        ss.pop();

        Symbol symbol = symbolTable.get(symbolStack.pop(), symbolStack.pop());
        VarType t = symbol.type == SymbolType.Bool ? VarType.BOOL : VarType.INT;
        ss.push(new Address(symbol.address, t));
    }

    private void kpid(Token next) {
        ss.push(symbolTable.get(next.value));
    }

    private void intpid(Token next) {
        ss.push(new Address(Integer.parseInt(next.value), VarType.INT, TypeAddress.Imidiate));
    }

    private void startCall() {
        //TODO: method ok
        ss.pop();
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();
        symbolTable.startCall(className, methodName);
        callStack.push(className);
        callStack.push(methodName);
    }

    private void call() {
        //TODO: method ok
        String methodName = callStack.pop();
        String className = callStack.pop();
        try {
            symbolTable.getNextParam(className, methodName);
            ErrorUtils.printError("The few argument pass for method");
        } catch (IndexOutOfBoundsException ignored) {
        }
        SymbolType symbolType = symbolTable.getMethodReturnType(className, methodName);
        VarType t = symbolType == SymbolType.Bool ? VarType.BOOL : VarType.INT;
        Address temp = new Address(memory.getTemp(), t);
        ss.push(temp);

        memory.addThreeAddressCode(Operation.ASSIGN, new Address(temp.num, VarType.ADDRESS, TypeAddress.Imidiate), new Address(symbolTable.getMethodReturnAddress(className, methodName), VarType.ADDRESS), null);
        memory.addThreeAddressCode(Operation.ASSIGN, new Address(memory.getCurrentCodeBlockAddress() + 2, VarType.ADDRESS, TypeAddress.Imidiate), new Address(symbolTable.getMethodCallerAddress(className, methodName), VarType.ADDRESS), null);
        memory.addThreeAddressCode(Operation.JP, new Address(symbolTable.getMethodAddress(className, methodName), VarType.ADDRESS), null, null);
    }

    private void arg() {
        String methodName = callStack.pop();
        try {
            Symbol symbol = symbolTable.getNextParam(callStack.peek(), methodName);
            VarType t = symbol.type == SymbolType.Bool ? VarType.BOOL : VarType.INT;
            Address param = ss.pop();
            if (param.varType != t) {
                ErrorUtils.printError("The argument type isn't match");
            }
            memory.addThreeAddressCode(Operation.ASSIGN, param, new Address(symbol.address, t), null);

        } catch (IndexOutOfBoundsException e) {
            ErrorUtils.printError("Too many arguments pass for method");
        }
        callStack.push(methodName);

    }

    private void assign() {

        Address s1 = ss.pop();
        Address s2 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorUtils.printError("The type of operands in assign is different ");
        }
        memory.addThreeAddressCode(Operation.ASSIGN, s1, s2, null);
    }

    private void add() {
        Address temp = new Address(memory.getTemp(), VarType.INT);
        Address s2 = ss.pop();
        Address s1 = ss.pop();

        if (s1.varType != VarType.INT || s2.varType != VarType.INT) {
            ErrorUtils.printError("In add two operands must be integer");
        }
        memory.addThreeAddressCode(Operation.ADD, s1, s2, temp);
        ss.push(temp);
    }


    private void sub() {
        Address temp = new Address(memory.getTemp(), VarType.INT);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.INT || s2.varType != VarType.INT) {
            ErrorUtils.printError("In sub two operands must be integer");
        }
        memory.addThreeAddressCode(Operation.SUB, s1, s2, temp);
        ss.push(temp);
    }

    private void mult() {
        Address temp = new Address(memory.getTemp(), VarType.INT);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.INT || s2.varType != VarType.INT) {
            ErrorUtils.printError("In mult two operands must be integer");
        }
        memory.addThreeAddressCode(Operation.MULT, s1, s2, temp);
        ss.push(temp);
    }

    private void label() {
        ss.push(new Address(memory.getCurrentCodeBlockAddress(), VarType.ADDRESS));
    }

    private void save() {
        ss.push(new Address(memory.saveMemory(), VarType.ADDRESS));
    }

    private void whileLoop() {
        memory.addThreeAddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress() + 1, VarType.ADDRESS), null);
        memory.addThreeAddressCode(Operation.JP, ss.pop(), null, null);
    }

    private void saveJpf() {
        Address save = new Address(memory.saveMemory(), VarType.ADDRESS);
        memory.addThreeAddressCode(ss.pop().num, Operation.JPF, ss.pop(), new Address(memory.getCurrentCodeBlockAddress(), VarType.ADDRESS), null);
        ss.push(save);
    }

    private void jpHere() {
        memory.addThreeAddressCode(ss.pop().num, Operation.JP, new Address(memory.getCurrentCodeBlockAddress(), VarType.ADDRESS), null, null);
    }

    private void print() {
        memory.addThreeAddressCode(Operation.PRINT, ss.pop(), null, null);
    }

    private void equal() {
        Address temp = new Address(memory.getTemp(), VarType.BOOL);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != s2.varType) {
            ErrorUtils.printError("The type of operands in equal operator is different");
        }
        memory.addThreeAddressCode(Operation.EQ, s1, s2, temp);
        ss.push(temp);
    }

    private void lessThan() {
        Address temp = new Address(memory.getTemp(), VarType.BOOL);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.INT || s2.varType != VarType.INT) {
            ErrorUtils.printError("The type of operands in less than operator is different");
        }
        memory.addThreeAddressCode(Operation.LT, s1, s2, temp);
        ss.push(temp);
    }

    private void and() {
        Address temp = new Address(memory.getTemp(), VarType.BOOL);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.BOOL || s2.varType != VarType.BOOL) {
            ErrorUtils.printError("In and operator the operands must be boolean");
        }
        memory.addThreeAddressCode(Operation.AND, s1, s2, temp);
        ss.push(temp);

    }

    private void not() {
        Address temp = new Address(memory.getTemp(), VarType.BOOL);
        Address s2 = ss.pop();
        Address s1 = ss.pop();
        if (s1.varType != VarType.BOOL) {
            ErrorUtils.printError("In not operator the operand must be boolean");
        }
        memory.addThreeAddressCode(Operation.NOT, s1, s2, temp);
        ss.push(temp);

    }

    private void defClass() {
        ss.pop();
        symbolTable.addClass(symbolStack.peek());
    }

    private void defMethod() {
        ss.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethod(className, methodName, memory.getCurrentCodeBlockAddress());

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    private void popClass() {
        symbolStack.pop();
    }

    private void extend() {
        ss.pop();
        symbolTable.setSuperClass(symbolStack.pop(), symbolStack.peek());
    }

    private void defField() {
        ss.pop();
        symbolTable.addField(symbolStack.pop(), symbolStack.peek());
    }

    private void defVar() {
        ss.pop();

        String var = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodLocalVariable(className, methodName, var);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    private void methodReturn() {
        String methodName = symbolStack.pop();
        Address s = ss.pop();
        SymbolType symbolType = symbolTable.getMethodReturnType(symbolStack.peek(), methodName);
        VarType t = symbolType == SymbolType.Bool ? VarType.BOOL : VarType.INT;
        if (s.varType != t) {
            ErrorUtils.printError("The type of method and return address was not match");
        }
        memory.addThreeAddressCode(
                Operation.ASSIGN,
                s,
                new Address(
                        symbolTable.getMethodReturnAddress(symbolStack.peek(), methodName),
                        VarType.ADDRESS,
                        TypeAddress.Indirect
                ),
                null
        );
        memory.addThreeAddressCode(
                Operation.JP,
                new Address(symbolTable.getMethodCallerAddress(symbolStack.peek(), methodName), VarType.ADDRESS),
                null,
                null
        );
    }

    private void defParam() {
        ss.pop();
        String param = symbolStack.pop();
        String methodName = symbolStack.pop();
        String className = symbolStack.pop();

        symbolTable.addMethodParameter(className, methodName, param);

        symbolStack.push(className);
        symbolStack.push(methodName);
    }

    private void lastTypeBool() {
        symbolTable.setLastType(SymbolType.Bool);
    }

    private void lastTypeInt() {
        symbolTable.setLastType(SymbolType.Int);
    }
}

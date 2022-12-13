package F4;

import java.util.*;

import F3.PostfixEvaluator.SyntaxErrorException;

public class InfixToPostfix {
    private static final String OPERATORS = "-+*/()";
    private static final int[] PRECEDENCE = {1, 1, 2, 2, -1, -1};


    private static final String PATTERN = "\\d+\\.\\d*|\\d+|" + "\\p{L}[\\p{L}\\p{N}]*" + "|[" + OPERATORS + "]";

    private final Deque<Character> operatorStack = new ArrayDeque<>();
    private final Stack<Integer> operandStack = new Stack<Integer>();

    public static int evaluate(String infix) throws SyntaxErrorException {
        return new InfixToPostfix().convertToPostfixAndCalc(infix);
    }

    private int convertToPostfixAndCalc(String infix) throws SyntaxErrorException {
        try {
            String nextToken;
            Scanner scan = new Scanner(infix);
            while ((nextToken = scan.findInLine(PATTERN)) != null) {
                char firstChar = nextToken.charAt(0);

                if (isOperand(firstChar)) {
                    handlePostfixOperand(Integer.parseInt(nextToken));
                } else if (isOperator(firstChar)) {
                    processOperator(firstChar);
                } else {
                    throw new SyntaxErrorException("Unexpected Character Encountered: " + firstChar);
                }
            }

            while (!operatorStack.isEmpty()) {
                char nextOperator = operatorStack.pop();
                if (nextOperator == '(') {
                    throw new SyntaxErrorException("Unmatched opening parenthesis");
                }
                handlePostfixOperator(nextOperator);
            }

            int result = operandStack.pop();
            if (!operandStack.empty()) {
                throw new SyntaxErrorException("Stack was badly formatted; did not follow digit-digit-operator pattern");
            }
            return result;

        } catch (NoSuchElementException ex) {
            throw new SyntaxErrorException("Syntax Error: The stack is empty");
        }
    }

    private void processOperator(char operator) throws SyntaxErrorException {
        if (operatorStack.isEmpty() || operator == '(') {
            operatorStack.push(operator);
        } else {
            char top = operatorStack.peek();
            if (precedence(operator) > precedence(top)) {
                operatorStack.push(operator);
            } else {
                while (!operatorStack.isEmpty() && precedence(operator) <= precedence(top)) {
                    operatorStack.pop();
                    if (top == '(') {
                        // Matching '(' popped ‐ exit loop.
                        break;
                    }
                    handlePostfixOperator(top);
                    if (!operatorStack.isEmpty()) {
                        // Reset topOp.
                        top = operatorStack.peek();
                    }
                }

                if (operator != ')') {
                    operatorStack.push(operator);
                }
            }
        }
    }

    private int evalOp(char op) {
        int rhs = operandStack.pop();
        int lhs = operandStack.pop();
        return switch (op) {
            case '+' -> lhs + rhs;
            case '-' -> lhs - rhs;
            case '*' -> lhs * rhs;
            case '/' -> lhs / rhs;
            default -> 0;
        };
    }

    private void handlePostfixOperand(int value) {
        operandStack.push(value);
    }

    private void handlePostfixOperator(char character) throws SyntaxErrorException {
        if (operandStack.size() < 2) {
            throw new SyntaxErrorException("Tried to use operator on fewer than 2 values");
        }
        operandStack.push(evalOp(character));
    }

    private boolean isOperand(char character) {
        return Character.isLetter(character) || Character.isDigit(character);
    }

    private boolean isOperator(char character) {
        return OPERATORS.indexOf(character) != -1;
    }

    private int precedence(char character) {
        int index = OPERATORS.indexOf(character);
        return PRECEDENCE[index];
    }

    // Insert isOperator and precedence here. See Listing 4.7.
}
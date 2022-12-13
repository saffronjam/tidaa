package F3;

import java.util.Stack;
import java.util.EmptyStackException;

public class PostfixEvaluator {
    public static class SyntaxErrorException extends Exception {
        public SyntaxErrorException(String message) {
            super(message);
        }
    }

    private static final String OPERATORS = "+-*/";
    private Stack<Integer> operandStack;

    public PostfixEvaluator() {
        operandStack = new Stack<Integer>();
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

    public static int staticEval(String expression) throws SyntaxErrorException {
        return new PostfixEvaluator().eval(expression);
    }

    private boolean isOperator(char ch) {
        return OPERATORS.indexOf(ch) != -1;
    }

    public int eval(String expression) throws SyntaxErrorException {
        // Delar upp strängen vid mellanslag
        String[] tokens = expression.split(" +");
        try {
            for (String nextToken : tokens) {
                var character = nextToken.charAt(0);
                if (Character.isDigit(character)) {
                    operandStack.push(Integer.parseInt(nextToken));
                } else if (isOperator(character)) {
                    if (operandStack.size() < 2) {
                        throw new SyntaxErrorException("Tried to use operator on fewer than 2 values");
                    }
                    operandStack.push(evalOp(character));
                } else {
                    throw new SyntaxErrorException("Invalid character encountered");
                }
            }
            int result = operandStack.pop();
            if (!operandStack.empty()) {
                throw new SyntaxErrorException("Stack was badly formatted; did not follow digit-digit-operator pattern");
            }
            return result;
        } catch (EmptyStackException ex) {
            throw new SyntaxErrorException("Syntax Error: The stack is empty");
        }
    }
}
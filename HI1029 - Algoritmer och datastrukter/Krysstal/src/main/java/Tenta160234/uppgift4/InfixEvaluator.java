package Tenta160234.uppgift4;

import java.util.Stack;
import java.util.EmptyStackException;

public class InfixEvaluator {

	public static class SyntaxErrorException extends Exception {

        SyntaxErrorException(String message) {
            super(message);
        }
    }

	private static final String OPERATORS = "+-*/";


    private boolean isOperator(char ch) {
        return OPERATORS.indexOf(ch) != -1;
    }

    public int eval(String expression) throws SyntaxErrorException {

    	String[] tokens = expression.split(" +");

        try {
        	for(String nextToken : tokens){
                if (Character.isDigit(nextToken.charAt(0))) {
                	int number = Integer.parseInt(nextToken);
                }
                else if (isOperator(nextToken.charAt(0))) {

                } else {
                    throw new SyntaxErrorException("Invalid expression encountered");
                }
            }

        } catch (EmptyStackException ex) {
            throw new SyntaxErrorException("Syntax Error: The stack is empty");
        }
        return 0;
    }
}
package F4;

import F3.PostfixEvaluator;

import java.util.Scanner;

public class TesterInfixConverter {
    public static void main(String[] args) {
        var converter = new InfixToPostfix();
        String line;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Enter a postfix expression to evaluate");
            line = in.nextLine();
            if (!line.equals("")) {
                try {
                    int result = InfixToPostfix.evaluate(line);
                    System.out.println("Value is " + result);
                } catch (PostfixEvaluator.SyntaxErrorException ex) {
                    System.out.println("Syntax error " + ex.getMessage());
                } catch (NumberFormatException nfe) {
                    System.out.println("Number format error " + nfe.getMessage());
                }
            } else {
                break;
            }
        }
    }
}

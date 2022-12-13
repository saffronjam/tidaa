package F3;

import java.util.Scanner;

public class Test7_1 {
    public static void main(String[] args) {
        PostfixEvaluator evaluator = new PostfixEvaluator();
        String line;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Enter a postfix expression to evaluate");
            line = in.nextLine();
            if (!line.equals("")) {
                try {
                    int result = evaluator.eval(line);
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

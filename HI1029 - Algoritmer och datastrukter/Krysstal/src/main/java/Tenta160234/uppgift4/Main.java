package Tenta160234.uppgift4;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        InfixEvaluator evaluator = new InfixEvaluator();

        String line;
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("Enter a infix expression without parenthesises to evaluate");
            line = in.nextLine();
            if(!line.equals("")){
                try {
                    int result = evaluator.eval(line);
                    System.out.println("Value is " + result);
                } catch (InfixEvaluator.SyntaxErrorException ex) {
                    System.out.println("Syntax error " + ex.getMessage());
                }
            } else {
                break;
            }
        }
        in.close();
    }
}
package F13;

import java.util.Arrays;
import java.util.Scanner;

public class TesterExchangerDQ {
    public static void main(String[] args) {

        var scanner = new Scanner(System.in);

        System.out.println("Please enter number of bills, enter -1 to use 1000, 500, 100, 50, 20, 10, 5, 1");
        int noBills = scanner.nextInt();
        int[] billsArray = null;
        if (noBills == -1) {
            billsArray = new int[]{1000, 500, 100, 50, 20, 10, 5, 1};
        } else {
            billsArray = new int[noBills];
            for (int i = 0; i < noBills; i++) {
                System.out.print("Enter bill no " + i + ": ");
                billsArray[i] = scanner.nextInt();
            }
        }
        System.out.println("Enter what you want to exchange: ");
        var toExchange = scanner.nextInt();
        var myExchanger = new ExchangerDQ(billsArray);
        System.out.println("Working on it...");
        long startTime = System.nanoTime();
        var result = myExchanger.exchange(toExchange);
        if (result == null) {
            System.out.println("Failed to find perfect solution");
        } else {
            for (int i = 0; i < result.length; i++) {
                System.out.print(billsArray[i] + ":" + result[i] + " ");
            }
        }
        long stopTime = System.nanoTime();
        System.out.println("\nTook: " + ((stopTime - startTime) / 1000000) + " ms");
    }
}

package F12;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TesterKnapsack {
    public static void main(String[] args) {

        System.out.println("Welcome to Kanpsacker! Please chose packing alternative: ");
        System.out.println("(1): Try predefined knapsack (size = 200) with predefined items");
        System.out.println("(2): Create your own");

        var scanner = new Scanner(System.in);
        {
            int choice = -1;
            boolean goodAnswer = false;
            while (!goodAnswer) {
                choice = scanner.nextInt();
                if (choice == 1 || choice == 2) {
                    goodAnswer = true;
                } else {
                    System.out.println("Bad input!");
                }
            }

            switch (choice) {
                case 1 -> {
                    Knapsack.Item[] items = new Knapsack.Item[]{
                            new Knapsack.Item("Cup", 30, 5),
                            new Knapsack.Item("Stick", 100, 2),
                            new Knapsack.Item("Pants", 40, 7),
                            new Knapsack.Item("Phone", 80, 14),
                            new Knapsack.Item("MP3", 70, 9)
                    };
                    executeKnapsack(200, items);
                }
                case 2 -> {
                    int knapSize = -1;
                    ArrayList<Knapsack.Item> items = new ArrayList<>();
                    System.out.println("Enter knap size: ");
                    knapSize = scanner.nextInt();

                    boolean wantMoreItems = true;
                    while (wantMoreItems) {
                        System.out.println("Add item options");
                        System.out.println("-Add item like: <name:string> <weight:float> <value:float>");
                        System.out.println("-Enter 'DONE' to stop adding and compute knapsack");

                        goodAnswer = false;

                        while (!goodAnswer) {
                            var answer = scanner.nextLine();

                            if (answer.length() > 0) {
                                var answerSplit = answer.split("\\W+");

                                if (answerSplit.length == 1 && answerSplit[0].equals("DONE")) {
                                    goodAnswer = true;
                                    wantMoreItems = false;
                                } else if (answerSplit.length == 3) {
                                    try {
                                        var name = answerSplit[0];
                                        var weight = Float.parseFloat(answerSplit[1]);
                                        var value = Float.parseFloat(answerSplit[2]);
                                        items.add(new Knapsack.Item(name, weight, value));
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Bad answer, please try again");
                                    }
                                } else {
                                    System.out.println("Bad answer, please try again");
                                }
                            }

                        }
                    }
                    Knapsack.Item[] itemsArray = new Knapsack.Item[items.size()];
                    executeKnapsack(knapSize, items.toArray(itemsArray));
                }
            }

        }
    }

    static void executeKnapsack(int size, Knapsack.Item[] items) {
        var myKnapsack = new Knapsack(size);
        System.out.println("Size: " + size);
        System.out.println("=== Items ===");
        for (var item : items) {
            System.out.println(item);
        }
        System.out.println("=============");

        var result = myKnapsack.pack(items);
        System.out.println(result);
    }
}

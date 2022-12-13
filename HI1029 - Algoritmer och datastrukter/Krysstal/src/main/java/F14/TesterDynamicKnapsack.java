package F14;

import java.util.ArrayList;
import java.util.Scanner;

public class TesterDynamicKnapsack {
    public static void main(String[] args) {

        System.out.println("Welcome to Knapsacker! Please chose packing alternative: ");
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
                    DynamicKnapsack.Item[] items = new DynamicKnapsack.Item[]{
                            new DynamicKnapsack.Item("Cup", 30, 5),
                            new DynamicKnapsack.Item("Stick", 100, 2),
                            new DynamicKnapsack.Item("Pants", 40, 7),
                            new DynamicKnapsack.Item("Phone", 80, 14),
                            new DynamicKnapsack.Item("MP3", 70, 9),

                            new DynamicKnapsack.Item("Pants", 40, 7),
                            new DynamicKnapsack.Item("Phone", 80, 14),
                            new DynamicKnapsack.Item("MP3", 70, 9),
                            new DynamicKnapsack.Item("Pants", 40, 7),
                            new DynamicKnapsack.Item("Phone", 80, 14),
                            new DynamicKnapsack.Item("MP3", 70, 9),

                    };
                    executeDynamicKnapsack(20000, items);
                }
                case 2 -> {
                    int knapSize = -1;
                    ArrayList<DynamicKnapsack.Item> items = new ArrayList<>();
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
                                        var weight = Integer.parseInt(answerSplit[1]);
                                        var value = Integer.parseInt(answerSplit[2]);
                                        items.add(new DynamicKnapsack.Item(name, weight, value));
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("Bad answer, please try again");
                                    }
                                } else {
                                    System.out.println("Bad answer, please try again");
                                }
                            }

                        }
                    }
                    DynamicKnapsack.Item[] itemsArray = new DynamicKnapsack.Item[items.size()];
                    executeDynamicKnapsack(knapSize, items.toArray(itemsArray));
                }
            }

        }
    }

    static void executeDynamicKnapsack(int size, DynamicKnapsack.Item[] items) {
        var myDynamicKnapsack = new DynamicKnapsack(size);
        System.out.println("Size: " + size);
        System.out.println("=== Items ===");
        for (var item : items) {
            System.out.println(item);
        }
        System.out.println("=============");

        var begin = System.nanoTime();
        var result = myDynamicKnapsack.pack(items);
        var end = System.nanoTime();

        System.out.println("Done in " + (end - begin) / 1000 + " us");
        System.out.println("Value: " + result);
    }
}

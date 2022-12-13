package F12;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Knapsack {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static class Item {
        public String name;
        public float weight;
        public float value;

        public Item(String name, float weight, float value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }

        @Override
        public String toString() {
            return ANSI_BLUE + name + " \t" + ANSI_RED + weight + ANSI_RESET + "|" + ANSI_GREEN + value + ANSI_RESET;
        }
    }

    public static class PackResult {
        public final HashMap<String, Integer> items = new HashMap<>();
        public float totalValue = 0.0f;
        public float totalWeight = 0.0f;

        public void addItem(Item item) {
            items.put(item.name, items.containsKey(item.name) ? items.get(item.name) + 1 : 1);
            totalValue += item.value;
            totalWeight += item.weight;
        }

        @Override
        public String toString() {
            var builder = new StringBuilder();
            builder.append("Total weight: ").append(totalWeight).append("\nTotal value: ").append(totalValue);
            for (var entry : items.entrySet()) {
                builder.append("\n").append(entry.getValue()).append(" of ").append(entry.getKey());
            }
            return builder.toString();
        }
    }

    private final int size;

    public Knapsack(int size) {
        this.size = size;
    }

    public PackResult pack(Item[] items) {
        Arrays.sort(items, Comparator.comparingDouble(item -> item.weight / item.value));
        var sizeLeft = size;
        var packResult = new PackResult();
        for (var item : items) {
            while (item.weight < sizeLeft) {
                packResult.addItem(item);
                sizeLeft -= item.weight;
            }
        }
        return packResult;
    }
}

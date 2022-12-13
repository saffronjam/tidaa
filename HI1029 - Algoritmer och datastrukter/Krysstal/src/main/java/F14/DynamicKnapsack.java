package F14;

import java.util.HashMap;
import java.util.List;

public class DynamicKnapsack {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    static class Item {
        public String name;
        public int weight;
        public int value;

        public Item(String name, int weight, int value) {
            this.name = name;
            this.weight = weight;
            this.value = value;
        }

        @Override
        public String toString() {
            return ANSI_BLUE + name + " \t" + ANSI_RED + weight + ANSI_RESET + "|" + ANSI_GREEN + value + ANSI_RESET;
        }
    }

    int size;

    public DynamicKnapsack(int size) {
        this.size = size;
    }

    public int pack(Item[] items) {
        return packTopDown(items, 0, size, 0, new HashMap<>());
    }

    private int packTopDown(Item[] items, int index, int sizeLeft, int value, HashMap<Integer, Integer> hashMap) {
        if (sizeLeft < 0) {
            return Integer.MIN_VALUE;
        }

        int maxValue = 0;
        for (int i = index; i < items.length; i++) {
            var candidate = value + items[i].value;
            if (hashMap.containsKey(sizeLeft) && hashMap.get(sizeLeft) >= candidate) {
                continue;
            }
            hashMap.put(sizeLeft, candidate);

            var result = items[i].value + packTopDown(items, index + 1, sizeLeft - items[i].weight, candidate, hashMap);
            if (result > 0 && result > maxValue) {
                maxValue = result;
            }
        }
        return maxValue;
    }
}

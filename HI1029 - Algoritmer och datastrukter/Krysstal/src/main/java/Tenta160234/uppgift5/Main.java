package Tenta160234.uppgift5;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(maxValue(20, new int[]{13, 11, 10, 5, 4}, new int[]{9, 8, 7, 4, 3})); //28
        System.out.println(maxValue(117, new int[]{13, 11, 10, 5, 4}, new int[]{9, 8, 7, 4, 3})); //169
        System.out.println(maxValue(30, new int[]{10, 22}, new int[]{10, 21})); //30
    }

    static int maxValue(int size, int[] values, int[] weights) {

        int[] partialSacks = new int[size + 1];
        int noItems = values.length;

        for (int sackSize = 0; sackSize <= size; sackSize++) {
            for (int itemIndex = 0; itemIndex < noItems; itemIndex++) {
                var weight = weights[itemIndex];
                if (sackSize - weight > 0) {
                    if (partialSacks[weight] + values[itemIndex] > partialSacks[sackSize - weight]) {
                        partialSacks[sackSize - weight] = partialSacks[weight] + values[itemIndex];
                    }
                }
            }
        }
        return weights[size];
    }

}
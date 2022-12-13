package F14;

import java.util.HashMap;

public class CoinMachineDynamic {

    public enum Strategy {
        NativeRecursion,
        DynamicTopDown
    }

    private int swaps = 0;

    public int solve(int goal, Strategy strategy) {
        swaps = 0;
        return switch (strategy) {
            case NativeRecursion -> solveNativeRecursive(1, goal);
            case DynamicTopDown -> solveDynamicRecursive(1, 0, goal, new HashMap<>());
        };
    }

    private int solveNativeRecursive(int points, int goal) {
        swaps++;
        if (points > goal) {
            return -1;
        }
        if (points == goal) {
            return 0;
        }

        int plus = solveNativeRecursive(points + 4, goal);
        int mult = solveNativeRecursive(points * 3, goal);
        if (plus < 0 && mult < 0) {
            return -1;
        }
        if (plus < 0) {
            return mult + 10;
        }
        if (mult < 0) {
            return plus + 5;
        }
        return Math.min(plus + 5, mult + 10);
    }

    private int solveDynamicRecursive(int points, int cost, int goal, HashMap<Integer, Integer> hashMap) {
        swaps++;
        if (points > goal) {
            return Integer.MAX_VALUE / 2;
        }
        if (points == goal) {
            return cost;
        }

        int plus = continueWithHashMap(points + 4, cost + 5, goal, hashMap);
        int mult = continueWithHashMap(points * 3, cost + 10, goal, hashMap);
        return Math.min(plus, mult);
    }

    private int continueWithHashMap(int points, int cost, int goal, HashMap<Integer, Integer> hashMap) {
        if (hashMap.containsKey(points)) {
            if (cost < hashMap.get(points)) {
                hashMap.put(points, cost);
                return solveDynamicRecursive(points, cost, goal, hashMap);
            }
            return Integer.MAX_VALUE / 2;
        }
        hashMap.put(points, cost);
        return solveDynamicRecursive(points, cost, goal, hashMap);
    }

    public int getSwaps() {
        return swaps;
    }
}

package F14;

import java.util.HashMap;

public class CrazyElevator {

    public enum Strategy {
        NativeRecursion,
        DynamicTopDown
    }

    int up, down, mult, topFloor, maxDepth;
    int buttonCount = 0;

    public CrazyElevator(int up, int down, int mult, int topFloor, int maxDepth) {
        this.up = up;
        this.down = down;
        this.mult = mult;
        this.topFloor = topFloor;
        this.maxDepth = maxDepth;
    }

    public int takeMeTo(int destinationFloor, Strategy strategy) {
        buttonCount = 0;
        return switch (strategy) {
            case NativeRecursion -> takeMeToNativeRecursion(0, destinationFloor, 0);
            case DynamicTopDown -> takeMeToDynamicTopdown(0, destinationFloor, 0, new HashMap<>());
        };
    }

    private int takeMeToNativeRecursion(int currentFloor, int destinationFloor, int noAttempts) {
        buttonCount++;
        if (noAttempts == maxDepth || destinationFloor == currentFloor) {
            return 0;
        }

        int tryUp = tryIfInRange(currentFloor + up, destinationFloor, noAttempts + 1);
        int tryDown = tryIfInRange(currentFloor - down, destinationFloor, noAttempts + 1);
        int tryMult = tryIfInRange(currentFloor * mult, destinationFloor, noAttempts + 1);

        return 1 + min(tryUp, tryDown, tryMult);
    }

    private int tryIfInRange(int floor, int destinationFloor, int noAttempts) {
        if (floor >= 0 && floor <= topFloor) {
            return takeMeToNativeRecursion(floor, destinationFloor, noAttempts);
        }
        return Integer.MAX_VALUE / 2;
    }


    private int takeMeToDynamicTopdown(int currentFloor, int destinationFloor, int noAttempts, HashMap<Integer, Integer> hashMap) {
        buttonCount++;
        if (currentFloor == destinationFloor) {
            return noAttempts;
        }
        if (noAttempts == maxDepth) {
            return Integer.MAX_VALUE / 2;
        }

        int tryUp = continueWithHashMap(currentFloor + up, destinationFloor, noAttempts + 1, hashMap);
        int tryDown = continueWithHashMap(currentFloor - down, destinationFloor, noAttempts + 1, hashMap);
        int tryMult = continueWithHashMap(currentFloor * mult, destinationFloor, noAttempts + 1, hashMap);

        return min(tryUp, tryDown, tryMult);
    }

    private int continueWithHashMap(int to, int destinationFloor, int noAttempts, HashMap<Integer, Integer> hashMap) {
        if (to < 0 || to > topFloor) {
            return Integer.MAX_VALUE / 2;
        }

        if (hashMap.containsKey(to)) {
            if (noAttempts < hashMap.get(to)) {
                hashMap.put(to, noAttempts);
                return takeMeToDynamicTopdown(to, destinationFloor, noAttempts, hashMap);
            }
            return Integer.MAX_VALUE / 2;

        }
        hashMap.put(to, noAttempts);
        return takeMeToDynamicTopdown(to, destinationFloor, noAttempts, hashMap);
    }

    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public int getButtonCount() {
        return buttonCount;
    }
}

package LabA;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;

public class TesterRadixSort {
    public static void main(String[] args) {
        var radix = new RadixSort();

        var arr = randomIntList(1000000, 1000000);

        long begin = System.currentTimeMillis();
        radix.sort(arr);
        long end = System.currentTimeMillis();
        System.out.println(Arrays.toString(arr));
        long dt = end - begin;
        System.out.println("Sorted in: " + dt + " ms");

    }

    public static int[] randomIntList(int size, int bound) {
        var ret = new int[size];
        var random = new Random();
        for (int i = 0; i < size; i++) {
            ret[i] = random.nextInt(bound);
        }
        return ret;
    }
}

package Tentaplugg;

import java.util.Arrays;
import java.util.Random;

public class QuickSort {

    public static void main(String[] args) {
        var intlist = randomIntList(1000, 10000);

        {
            var begin = System.nanoTime();
            F9.QuickSort.sort(intlist);
            var end = System.nanoTime();
            System.out.println("OLD: (Took: " + (end - begin) / 1000000 + "ms)");
        }

        {
            var begin = System.nanoTime();
            QuickSort.sort(intlist);
            var end = System.nanoTime();
            System.out.println("NEW: (Took: " + (end - begin) / 1000000 + "ms)");
        }
    }

    public static int[] randomIntList(int size, int bound) {
        var ret = new int[size];
        var random = new Random();
        for (int i = 0; i < size; i++) {
            ret[i] = random.nextInt(bound);
        }
        return ret;
    }

    public static void sort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    public static void sort(int[] arr, int left, int right) {
        if (left == right || left > right) return;

        int pivot = partion(arr, left, right);

        sort(arr, left, pivot - 1);
        sort(arr, pivot + 1, right);
    }

    private static int partion(int[] arr, int left, int right) {
        int slacker = left;
        int pivot = arr[right];

        for (int advancer = left; advancer < right; advancer++) {
            if (arr[advancer] < pivot) {
                swap(arr, slacker++, advancer);
            }
        }
        swap(arr, right, slacker);
        return slacker;
    }

    private static void swap(int[] arr, int first, int second) {
        var tmp = arr[first];
        arr[first] = arr[second];
        arr[second] = tmp;
    }
}

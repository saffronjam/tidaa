
package Tenta170314.uppgift4;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        int size = 10;
        int[] v = new int[size];
        for (int i = 0; i < v.length; i++) {
            v[i] = (int) (Math.random() * size * 10);
        }
        heapSort(v);
        System.out.println(Arrays.toString(v));
        for (int i = 0; i < v.length - 1; i++) {
            if (v[i] > v[i + 1]) {
                System.out.println("error");
                break;
            }
        }
    }

    public static void heapSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            offer(arr, i);
        }

        for (int i = 0; i < arr.length - 1; i++) {
            int insert = arr.length - i - 1;
            arr[insert] = poll(arr, insert);
        }
    }

    private static void offer(int[] arr, int index) {
        siftDown(arr, arr.length - index - 1, arr.length);
    }

    private static int poll(int[] arr, int index) {
        swap(arr, 0, index);
        siftDown(arr, 0, index);
        return arr[index];
    }

    private static void siftDown(int[] arr, int from, int to) {
        int largest = from;
        int leftChild = from * 2 + 1;
        int rightChild = from * 2 + 2;

        if (leftChild < to && arr[leftChild] > arr[largest]) {
            largest = leftChild;
        }
        if (rightChild < to && arr[rightChild] > arr[largest]) {
            largest = rightChild;
        }

        // If child is misplaced, swap with parent and siftdown from there recursively
        if (largest != from) {
            swap(arr, from, largest);
            siftDown(arr, largest, to);
        }
    }

    private static void swap(int[] arr, int first, int second) {
        var tmp = arr[first];
        arr[first] = arr[second];
        arr[second] = tmp;
    }

}

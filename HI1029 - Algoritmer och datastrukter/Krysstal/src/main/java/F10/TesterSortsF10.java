package F10;

import java.util.Arrays;
import java.util.Random;

public class TesterSortsF10 {
    public static void main(String[] args) {

        int noRandomNumbers = 10;

        System.out.println("--- Sortering ---");
        var heapSort = new HeapSort();
        var arr = randomIntList(noRandomNumbers);
        System.out.println("Från: " + Arrays.toString(arr));
        heapSort.sort(arr);
        System.out.println("Till: " + Arrays.toString(arr));


        System.out.println("\n--- Sätta in element ---");
        var heap = new Heap<Integer>(10);

        var random = new Random();
        System.out.print("Sätter in: ");
        for (int i = 0; i < 25; i++) {
            var insert = random.nextInt(64);
            System.out.print(insert + ", ");
            heap.insert( insert);
        }
        System.out.println("\nTräd:\n" + heap);
        System.out.println("Array:\n" + heap.toStringAsArray());

        System.out.println("\n--- Ta ut element ---");
        for (int i = 0; i < 3; i++) {
            System.out.println("Tog ut: " + heap.extract());
        }
        System.out.println("Efter:");
        System.out.println("Träd:\n" + heap);
        System.out.println("Array:\n" + heap.toStringAsArray());
    }

    public static int[] randomIntList(int size) {
        var ret = new int[size];
        var random = new Random();
        for (int i = 0; i < size; i++) {
            ret[i] = random.nextInt(10);
        }
        return ret;
    }
}

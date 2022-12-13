package F9;

import java.util.Arrays;
import java.util.Random;

public class TesterSorts {
    public static void main(String[] args) {

        int noRandomNumbers = 49;

        var selection = new SelectionSort();
        var arr = randomIntList(noRandomNumbers);
        selection.sort(arr);
        System.out.println("Selection sort: "  + Arrays.toString(arr));

        var insertion = new InsertionSort();
        arr = randomIntList(noRandomNumbers);
        insertion.sort(arr);
        System.out.println("Insertion sort: " + Arrays.toString(arr));

        var merge = new MergeSort();
        arr = randomIntList(noRandomNumbers);
        merge.sort(arr);
        System.out.println("Merge sort: \t" + Arrays.toString(arr));

        var shell = new ShellSort();
        arr = randomIntList(noRandomNumbers);
        shell.sort(arr);
        System.out.println("Shell sort: \t" + Arrays.toString(arr));

        var quick = new QuickSort();
        arr = randomIntList(noRandomNumbers);
        quick.sort(arr);
        System.out.println("Quick sort: \t" + Arrays.toString(arr));
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

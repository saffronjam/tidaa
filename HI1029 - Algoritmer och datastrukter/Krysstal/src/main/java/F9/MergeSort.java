package F9;

import java.util.Arrays;

public class MergeSort {

    public void sort(int[] arr) {
        if (arr.length <= 1) {
            return;
        }

        var lowerHalf = Arrays.copyOfRange(arr, 0, arr.length / 2);
        var upperHalf = Arrays.copyOfRange(arr, arr.length / 2, arr.length);

        sort(lowerHalf);
        sort(upperHalf);
        merge(lowerHalf, upperHalf, arr);
    }

    private void merge(int[] lower, int[] upper, int[] dest) {
        int indexLower = 0, indexUpper = 0, indexDest = 0;

        while (indexLower < lower.length && indexUpper < upper.length) {
            if (lower[indexLower] <= upper[indexUpper]) {
                dest[indexDest++] = lower[indexLower++];
            } else {
                dest[indexDest++] = upper[indexUpper++];
            }
        }

        while (indexLower < lower.length) {
            dest[indexDest++] = lower[indexLower++];
        }
        while (indexUpper < upper.length) {
            dest[indexDest++] = upper[indexUpper++];
        }
    }

}

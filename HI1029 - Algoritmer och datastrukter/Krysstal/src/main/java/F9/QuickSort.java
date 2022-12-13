package F9;

public class QuickSort {
    public static void sort(int[] arr) {
        sort(arr, 0, arr.length - 1);
    }

    private static void sort(int[] arr, int first, int last) {
        if (first < last) {
            int pivIndex = partition(arr, first, last);
            // Nedre halvan
            sort(arr, first, pivIndex - 1);
            // Över halvan
            sort(arr, pivIndex + 1, last);
        }
    }

    private static int partition(int[] arr, int first, int last) {
        // Vi väljer första elementet som pivot.
        // Detta är OK så länge vi kan anta att listan är HELT slumpad.
        // Vi skulle också ha kunnat välja medianen av 3,5 eller 7 olika element
        int pivot = arr[first];

        // Index för framlängssökning
        int low = first + 1;

        // Index för baklängssökning
        int high = last;

        while (high > low) {
            // Sök framlänges
            while (low <= high && arr[low] <= pivot) {
                low++;
            }
            // Sök baklänges
            while (low <= high && arr[high] > pivot) {
                high--;
            }

            // Sålänge high inte blivit mindre än low, byt
            if (high > low) {
                swap(low, high, arr);
            }
        }

        while (high > first && arr[high] >= pivot) {
            high--;
        }

        // Till sist, byt pivot med list[high]
        if (pivot > arr[high]) {
            arr[first] = arr[high];
            arr[high] = pivot;
            return high;
        } else {
            return first;
        }
    }

    private static void swap(int first, int second, int[] arr) {
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }

}

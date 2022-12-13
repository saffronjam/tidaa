package F9;

public class SelectionSort {
    public void sort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            swap(i, minIndex, arr);
        }
    }

    private static void swap(int first, int second, int[] arr){
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }
}

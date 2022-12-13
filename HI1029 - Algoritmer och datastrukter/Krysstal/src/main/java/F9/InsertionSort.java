package F9;

public class InsertionSort {
    public void sort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            var data = arr[i];
            var dataIndex = i;
            while (dataIndex > 0 && data < arr[dataIndex - 1]) {
                arr[dataIndex] = arr[dataIndex - 1];
                dataIndex--;
            }
            arr[dataIndex] = data;
        }
    }
}

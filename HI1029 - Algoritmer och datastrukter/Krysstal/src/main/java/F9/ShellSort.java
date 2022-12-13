package F9;

public class ShellSort {

    public void sort(int[] arr) {
        int gap = arr.length / 2;

        while (gap > 0) {
            for (int i = gap; i < arr.length; i++) {
                var data = arr[i];
                var dataIndex = i;
                while (dataIndex > gap - 1 && data < arr[dataIndex - gap]) {
                    arr[dataIndex] = arr[dataIndex - gap];
                    dataIndex -= gap;
                }
                arr[dataIndex] = data;
            }
            gap = gap == 2 ? 1 : (int) (gap / 2.2);
        }
    }
}

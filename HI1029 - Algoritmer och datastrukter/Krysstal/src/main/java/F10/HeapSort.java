package F10;

public class HeapSort {

    public void sort(int[] arr) {
        int heapSize = 1;
        // Första steget: skapa en heap av arrayen
        while (heapSize < arr.length) {
            offer(arr, heapSize++);
        }
        // Andra steget: Bryt stegvis ner heapen till en array
        //               genom att plocka ut roten
        while (heapSize > 1) {
            arr[--heapSize] = poll(arr, heapSize);
        }
    }

    private void offer(int[] arr, int heapSize) {
        siftDown(arr, arr.length, arr.length - heapSize - 1);
    }

    private int poll(int[] arr, int heapSize) {
        swap(0, heapSize, arr);
        siftDown(arr, heapSize, 0);
        return arr[heapSize];
    }

    private void siftDown(int[] arr, int maxIndex, int parentIndex) {
        int largest = parentIndex; // Anta först att parent är störst och att vi är i korrekt state
        int leftChild = 2 * parentIndex + 1;
        int rightChild = 2 * parentIndex + 2;

        // Tecknen i jämförelsen här bestämmer om vi ska sortera stigande eller fallande
        // >  stigande
        // <  fallande
        if (leftChild < maxIndex && arr[leftChild] > arr[largest]) {
            largest = leftChild;
        }
        if (rightChild < maxIndex && arr[rightChild] > arr[largest]) {
            largest = rightChild;
        }

        if (largest == parentIndex) return;

        swap(parentIndex, largest, arr);
        siftDown(arr, maxIndex, largest);

    }

    private static void swap(int first, int second, int[] arr) {
        int temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }
}

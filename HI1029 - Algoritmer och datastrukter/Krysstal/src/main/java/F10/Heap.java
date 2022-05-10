package F10;

import java.util.Arrays;

public class Heap<E extends Comparable<E>> {
    private E[] data;
    private int size;

    public Heap(int initialCapacity) {
        data = (E[]) new Comparable[initialCapacity];
    }

    public boolean insert(E element) {
        if (size == data.length) {
            resize(data.length * 2);
        }

        data[size] = element;
        siftUp(size++);
        return true;
    }

    public E extract() {
        var extracted = data[--size];
        swap(0, size, data);
        data[size] = null;
        siftDown(0);
        return extracted;
    }

    private void siftDown(int parentIndex) {
        int largest = parentIndex; // Anta först att parent är störst och att vi är i korrekt state
        int leftChild = 2 * parentIndex + 1;
        int rightChild = 2 * parentIndex + 2;

        // Tecknen här avgör om vi är max- eller min-heap (måste även ändras i "heapifyBottomUp()"
        // >  max-heap
        // <  min-heap
        if (leftChild < size && data[leftChild].compareTo(data[largest]) < 0) {
            largest = leftChild;
        }

        if (rightChild < size && data[rightChild].compareTo(data[largest]) < 0) {
            largest = rightChild;
        }

        if (largest == parentIndex) return;

        swap(parentIndex, largest, data);
        siftDown(largest);
    }

    private void siftUp(int child) {
        int parent = (child - 1) / 2;
        if (parent < 0 || data[parent] == null) return;

        // Tecknet här avgör om vi är max- eller min-heap (måste även ändras i "heapifyTopDown()"
        // >  max-heap
        // <  min-heap
        if (data[child].compareTo(data[parent]) < 0) {
            swap(child, parent, data);
            siftUp(parent);
        }
    }

    private void resize(int newCapacity) {
        data = Arrays.copyOf(data, newCapacity);
    }

    private static <E> void swap(int first, int second, E[] arr) {
        E temp = arr[first];
        arr[first] = arr[second];
        arr[second] = temp;
    }

    public String toStringAsArray() {
        return Arrays.toString(data);
    }

    @Override
    public String toString() {

        // Kod från https://stackoverflow.com/a/64535931/11793673
        // Hämtat: 2021-02-16 16:38
        // Skriven av: elkshadow5

        int maxDepth = (int) (Math.log(size) / Math.log(2));  // log base 2 of n

        StringBuilder hs = new StringBuilder();  // heap string builder
        for (int d = maxDepth; d >= 0; d--) {  // number of layers, we build this backwards
            int layerLength = (int) Math.pow(2, d);  // numbers per layer

            StringBuilder line = new StringBuilder();  // line string builder
            for (int i = layerLength; i < (int) Math.pow(2, d + 1); i++) {
                // before spaces only on not-last layer
                if (d != maxDepth) {
                    line.append(" ".repeat((int) Math.pow(2, maxDepth - d)));
                }
                // extra spaces for long lines
                int loops = maxDepth - d;
                if (loops >= 2) {
                    loops -= 2;
                    while (loops >= 0) {
                        line.append(" ".repeat((int) Math.pow(2, loops)));
                        loops--;
                    }
                }

                // add in the number
                if (i <= size) {
                    line.append(String.format("%-2s", data[i - 1]));  // add leading zeros
                } else {
                    line.append("--");
                }

                line.append(" ".repeat((int) Math.pow(2, maxDepth - d)));  // after spaces
                // extra spaces for long lines
                loops = maxDepth - d;
                if (loops >= 2) {
                    loops -= 2;
                    while (loops >= 0) {
                        line.append(" ".repeat((int) Math.pow(2, loops)));
                        loops--;
                    }
                }
            }
            hs.insert(0, line.toString() + "\n");  // prepend line
        }
        return hs.toString();
    }
}

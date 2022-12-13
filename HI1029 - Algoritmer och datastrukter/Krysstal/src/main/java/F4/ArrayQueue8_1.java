package F4;

import java.util.Arrays;

public class ArrayQueue8_1<E> {
    private int front, rear, size, maxSize;
    private E[] data;

    public ArrayQueue8_1(int initialSize) {
        size = 0;
        front = 0;
        maxSize = initialSize;
        rear = 0;
        data = (E[]) new Object[maxSize];
    }

    public boolean offer(E element) {
        if (size == maxSize) reallocate();
        data[rear] = element;
        // Fix 1
        rear = (rear + 1) % maxSize;
        // rear = rear + 1 % maxSize;
        size++;
        return true;
    }

    public E peek() {
        if (size == 0) return null;
        return data[front];
    }

    public E poll() {
        if (size == 0) {
            return null;
        } else {
            size--;
            E element = data[front];

            // Fix 3 tillagd
            data[front] = null;

            // Fix 1
            front = (front + 1) % maxSize;
            // rear = rear + 1 % maxSize;

            return element;
        }
    }

    private void reallocate() {
        // Fix 2
        int newMaxSize = maxSize * 2;

        var newData = (E[]) new Object[newMaxSize];
        int j = front;
        for (int i = 0; i < size; i++) {
            newData[i] = data[j];
            j = (j + 1) % maxSize;
        }
        front = 0;
        rear = size;
        maxSize = newMaxSize;
        data = newData;

        // maxSize *= 2;
        // data = Arrays.copyOf(data, maxSize);
    }

    @Override
    public String toString() {
        return "ArrayQueue8_1{" +
                "front=" + front +
                ", rear=" + rear +
                ", size=" + size +
                ", maxSize=" + maxSize +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

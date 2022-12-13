package F1;

import java.util.Arrays;

public class NArrayList<E> {
    private E[] data;
    private int size;
    private int capacity;

    NArrayList() {
        size = 0;
        capacity = 10;
        data = (E[]) new Object[capacity];
    }

    public boolean add(E element) {
        if (size == capacity) {
            reallocate();
        }
        data[size++] = element;
        return true;
    }

    public E get(int index) {
        if (index < 0 || index >= capacity) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return data[index];
    }

    public boolean set(E element, int index) {
        if (index < 0 || index >= capacity) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        data[index] = element;
        return true;
    }

    public void add(int index, E element) {
        if (index < 0 || index >= capacity) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        if (size == capacity) {
            reallocate();
        }

        if (size + 1 - index >= 0) {
            System.arraycopy(data, index, data, index + 1, size + 1 - index);
        }
        data[index] = element;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= capacity) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        if (size - 1 - index >= 0) {
            System.arraycopy(data, index + 1, data, index, size - 1 - index);
        }
        data[size--] = null;

        return true;
    }

    public boolean remove(E element) {
        int index = indexOf(element);
        return index != -1 && remove(index);
    }

    public int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "NArrayList{" +
                "data=" + Arrays.toString(data) +
                ", size=" + size +
                ", capacity=" + capacity +
                '}';
    }

    private void reallocate() {
        capacity *= 2;
        data = Arrays.copyOf(data, size);
    }
}

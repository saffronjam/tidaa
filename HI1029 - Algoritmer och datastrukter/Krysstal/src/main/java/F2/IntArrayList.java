package F2;

public class IntArrayList {
    private int[] data;
    private int size, capacity;

    IntArrayList(int initialCapacity) {
        size = 0;
        capacity = initialCapacity;
        data = new int[capacity];
    }

    public int get(int index) {
        throwIfBadIndex(index);
        return data[index];
    }

    public boolean set(int index, int element) {
        throwIfBadIndex(index);
        data[index] = element;
        return true;
    }

    public boolean add(int element) {
        return add(this.size, element);
    }

    public boolean add(int index, int element) {
        if (size == capacity) {
            reallocate();
        }

        throwIfBadIndex(index);

        for (int i = size - 1; i > index; i--) {
            data[i - 1] = data[i];
        }
        data[index] = element;
        size++;
        return true;
    }

    public boolean remove(int index) {
        throwIfBadIndex(index);
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = 0;
        return true;
    }

    public int indexOf(int element) {
        for (int i = 0; i < size; i++) {
            if (element == data[i]) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        var dataAsString = new StringBuilder();
        for (var element : data) {
            dataAsString.append(element).append(", ");
        }
        return "IntArrayList{" +
                "data: [" + dataAsString +
                "], size=" + size +
                ", capacity=" + capacity +
                '}';
    }

    private void throwIfBadIndex(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= capacity) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    private void reallocate() {
        capacity *= 2;
        var tmp = new int[capacity];
        for (int i = 0; i < size; i++) {
            tmp[i] = data[i];
        }
        data = tmp;
    }
}

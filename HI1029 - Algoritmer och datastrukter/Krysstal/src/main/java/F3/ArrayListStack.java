package F3;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class ArrayListStack<E> implements StackInt<E> {
    ArrayList<E> data;

    public ArrayListStack() {
        data = new ArrayList<E>(10);
    }

    @Override
    public E push(E obj) {
        data.add(obj);
        return obj;
    }

    @Override
    public E peek() {
        if (data.isEmpty()) {
            throw new EmptyStackException();
        }
        return data.get(data.size() - 1);
    }

    @Override
    public E pop() {
        if (data.isEmpty()) {
            throw new EmptyStackException();
        }
        int index = data.size() - 1;
        E elem = data.get(index);
        data.remove(index);
        return elem;
    }

    @Override
    public boolean empty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return "ArrayListStack{" +
                "data=" + data +
                '}';
    }
}

package F3;

import java.util.EmptyStackException;

public class LinkedStack<E> implements StackInt<E> {
    private static class Node<E> {
        private E data;
        private Node<E> next;

        private Node(E dataItem, Node<E> nodeRef) {
            data = dataItem;
            next = nodeRef;
        }
    }

    private Node<E> top;

    public LinkedStack() {
        top = null;
    }

    @Override
    public E push(E obj) {
        top = new Node<E>(obj, top);
        return obj;
    }

    public E pop() {
        if (empty()) {
            throw new EmptyStackException();
        } else {
            E result = top.data;
            top = top.next;
            return result;
        }
    }

    @Override
    public E peek() {
        if (empty()) {
            throw new EmptyStackException();
        } else {
            return top.data;
        }
    }

    public E peek(int n) {
        if (empty()) {
            throw new EmptyStackException();
        }

        var head = top;
        for (int i = 0; i < n; i++) {
            head = head.next;
            if (head == null) {
                throw new IndexOutOfBoundsException();
            }
        }
        return head.data;
    }

    @Override
    public boolean empty() {
        return top == null;
    }

    public int size() {
        int size = 0;
        for (var head = this.top; head != null; head = head.next) {
            size++;
        }
        return size;
    }

    public E flush() {
        if (empty()) {
            return null;
        }
        var head = top;
        while (head.next != null) {
            head = head.next;
        }
        top = null;
        return head.data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> p = top;
        if (p != null) {
            while (p.next != null) {
                sb.append(p.data.toString());
                sb.append(" -> ");
                p = p.next;
            }
            sb.append(p.data.toString());
        }
        sb.append("]");
        return sb.toString();
    }
}

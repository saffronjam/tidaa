package F4;

public class LinkedDequeue<E> {
    private static class Node<E> {
        private E data;
        private Node<E> prev;
        private Node<E> next;

        private Node(E dataItem, Node<E> prev, Node<E> next) {
            data = dataItem;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public E pollFirst() {
        if (head == null) {
            throw new IllegalStateException("Empty dequeue");
        }

        E ret;
        if (size == 1) {
            ret = tail.data;
            tail = null;
            head = null;
        } else {
            ret = head.data;
            head = head.next;
            head.prev = null;
        }
        size--;
        return ret;
    }

    public E pollLast() {
        if (tail == null) {
            throw new IllegalStateException("Empty dequeue");
        }

        E ret;
        if (size == 1) {
            ret = tail.data;
            tail = null;
            head = null;
        } else {
            ret = tail.data;
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return ret;
    }

    public boolean offerFirst(E data) {
        if (size == 0) {
            head = new Node<E>(data, null, null);
            tail = head;
        } else {
            head = new Node<E>(data, null, head);
            head.next.prev = head;
        }
        size++;
        return true;
    }

    public boolean offerLast(E data) {
        if (size == 0) {
            tail = new Node<E>(data, null, null);
            head = tail;
        } else {
            tail = new Node<E>(data, tail, null);
            tail.prev.next = tail;
        }
        size++;
        return true;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("[");
        if (head != null) {
            var current = head;
            while (current.next != null) {
                sb.append(current.data.toString());
                sb.append(" <=> ");
                current = current.next;
            }
            sb.append(current.data);
        }
        sb.append("]");
        return sb.toString();
    }
}

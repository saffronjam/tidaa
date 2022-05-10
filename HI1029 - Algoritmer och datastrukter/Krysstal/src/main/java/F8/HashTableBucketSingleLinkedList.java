package F8;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTableBucketSingleLinkedList<E> implements Iterable<E> {
    private static class Node<E> {
        private E data;
        private Node<E> next;

        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E> head;

    public HashTableBucketSingleLinkedList() {
        head = null;
    }

    public void addFirst(E item) {
        head = new Node<>(item, head);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("[");
        var p = head;
        if (p != null) {
            while (p.next != null) {
                sb.append(p.data.toString());
                sb.append(" => ");
                p = p.next;
            }
            sb.append(p.data.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    private class Itr implements Iterator<E> {//nested class – ligger i SingleLinkedList
        Node<E> current;
        Node<E> beforeCurrent;
        Node<E> beforeBeforeCurrent;

        public Itr(Node<E> start) {
            current = start;
            beforeCurrent = null;
            beforeBeforeCurrent = null;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (current == null) {
                throw new NoSuchElementException();
            }

            E returnValue = current.data;
            beforeBeforeCurrent = beforeCurrent;
            beforeCurrent = current;
            current = current.next;
            return returnValue;
        }

        @Override
        public void remove() {
            if (beforeCurrent == null || beforeCurrent == beforeBeforeCurrent) {
                throw new IllegalStateException("Call next() before remove()");
            }
            if (beforeBeforeCurrent != null) {
                beforeBeforeCurrent.next = current;
            } else {
                head = current;
            }
            beforeCurrent = beforeBeforeCurrent;
        }
    }

    public Iterator<E> iterator() {
        return new Itr(head);
    }

}

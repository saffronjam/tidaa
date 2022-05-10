package F6;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListGetNodeRecursive<E> implements Iterable<E> {
    private static class Node<E> {
        private E data;
        private Node<E> next;

        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public LinkedListGetNodeRecursive() {
        head = null;
        size = 0;
    }

    public void add(int index, E item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }

        if (index == 0) {
            addFirst(item);
        } else if (index == size) {
            addLast(item);
        } else {
            Node<E> node = getNode(index - 1);
            addAfter(node, item);
        }
    }

    public void addFirst(E item) {
        head = new Node<E>(item, head);
        if (size == 0) {
            tail = head;
        }
        size++;
    }

    public void addLast(E item) {
        var newNode = new Node<E>(item, null);
        if (size == 0) {
            tail = newNode;
            head = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    private void addAfter(Node<E> node, E item) {
        node.next = new Node<E>(item, node.next);
        size++;
    }

    private Node<E> getNode(int index) {
        return getNodeRecursive(index, 0, head);
    }

    private Node<E> getNodeRecursive(int index, int currentIndex, Node<E> currentNode) {
        if (currentIndex == index) {
            return currentNode;
        }
        if (currentNode == null) {
            return null;
        }
        return getNodeRecursive(index, currentIndex + 1, currentNode.next);
    }

    public boolean add(E item) {
        add(size, item);
        return true;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }

        if (index == size - 1) {
            return tail.data;
        }
        return getNode(index).data;
    }

    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }

        if (index == 0) {
            return removeFirst();
        } else if (index == size - 1) {
            return removeLast();
        } else {
            return removeAt(index);
        }
    }

    public E removeFirst() {
        var ret = head.data;
        head = head.next;
        if (size == 1) {
            tail = null;
        }
        size--;
        return ret;
    }

    public E removeLast() {
        var current = head;
        for (int i = 0; i < size - 2; i++) {
            current = current.next;
        }
        var oldTail = tail;
        tail = current;
        tail.next = null;
        size--;
        return oldTail.data;
    }

    public E removeAt(int index) {
        var current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        var ret = current.next.data;
        current.next = current.next.next;
        size--;
        return ret;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> p = head;
        if (p != null) {
            while (p.next != null) {
                sb.append(p.data.toString());
                sb.append(" ==> ");
                p = p.next;
            }
            sb.append(p.data.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    private class Itr implements Iterator<E> {//nested class – ligger i SingleLinkedList
        Node<E> current;

        public Itr(Node<E> start) {
            current = start;
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
            current = current.next;
            return returnValue;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Iterator<E> iterator() {
        return new Itr(head);
    }

}

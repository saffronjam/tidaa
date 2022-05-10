package F4;

import LabA.SingleLinkedListUppg2;

import java.util.EmptyStackException;

public class LinkedQueue<E> {
    private static class Node<E> {
        private E data;
        private Node<E> next;

        private Node(E dataItem, Node<E> nodeRef) {
            data = dataItem;
            next = nodeRef;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public LinkedQueue() {
        head = null;
        size = 0;
    }

    public boolean enqueue(E data) {
        var newNode = new Node<E>(data, null);
        if (size == 0) {
            tail = newNode;
            head = tail;
        } else {
            tail.next = newNode;
            tail = tail.next;
        }
        size++;
        return true;
    }

    public E dequeue() {
        if (head == null) {
            throw new IllegalStateException("Empty queue");
        }

        var ret = head.data;
        head = head.next;
        size--;
        if (size == 0) {
            tail = null;
        }
        return ret;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        var p = head;
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

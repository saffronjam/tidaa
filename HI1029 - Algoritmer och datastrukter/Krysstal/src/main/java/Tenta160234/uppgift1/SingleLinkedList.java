package Tenta160234.uppgift1;

public class SingleLinkedList<E> {

    private static class Node<E> {

        private E data;
        private Node<E> next;

        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }

    }

    private Node<E> head;

    public SingleLinkedList() {
        head = null;
    }

    private void addFirst(E item) {
        head = new Node<E>(item, head);
    }

    private void addAfter(Node<E> node, E item) {
        node.next = new Node<E>(item, node.next);
    }

    private Node<E> getNode(int index) {
        //denna metod ska returnera noden pa plats index.
        //Om index ar negativt returnerar den noden med index = 0
        //Om index ar storre an hogsta index returnerar den noden med hogst index
        //Om listan ar tom returnerar den null
        if (index <= 0) {
            return head;
        }

        Node<E> candidate = null;
        Node<E> current = head;
        for (int i = 0; i <= index && current != null; i++) {
            candidate = current;
            current = current.next;
        }
        return candidate;
    }

    public E get(int index) {
        //denna metod anropar rimligen getNode
        //Ska returnera elementet pa plats index
        //Om index ar negativt returneras elementet med index = 0
        //Om index ar storre an hogsta index returnerar den elementet med hogst index
        //Om listan ar tom returnerar den null
        var result = getNode(index);
        return result == null ? null : result.data;
    }

    public void add(int index, E item) {
        if (index <= 0 || head == null) {
            addFirst(item);
        } else {
            Node<E> node = getNode(index - 1);
            addAfter(node, item);
        }
    }


    public int size() {
        //Skriv kod. Detta ar rimligen en wrapper som anropar en privat hjalpmetod
        //hjalpmetoden ska sedan losa uppgiften rekursivt
        return size(head);
    }

    private int size(Node<E> node) {
        return node == null ? 0 : 1 + size(node.next);
    }

}

package F7;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BST<E extends Comparable<E>> {
    private static class Node<E> {
        private E data;
        private Node<E> left, right;

        private Node(E d) {
            data = d;
            left = right = null;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private Node<E> root;
    private E deletedData;

    public BST() {
        root = null;
    }

    private void inOrder(Node<E> node, StringBuilder sb) {
        if (node != null) {
            inOrder(node.left, sb);
            sb.append(": " + node.toString());
            inOrder(node.right, sb);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        inOrder(root, sb);
        return sb.toString();
    }

    private boolean add(E data, Node<E> node) {
        if (data.compareTo(node.data) == 0)
            return false;
        else if (data.compareTo(node.data) < 0)
            if (node.left == null) {
                node.left = new Node<E>(data);
                return true;
            } else
                return add(data, node.left);
        else if (node.right == null) {
            node.right = new Node<E>(data);
            return true;
        } else
            return add(data, node.right);
    }

    public boolean add(E data) {
        if (root == null) {
            root = new Node<E>(data);
            return true;
        } else
            return add(data, root);
    }

    private E find(E target, Node<E> node) {
        if (node == null)
            return null;
        if (target.compareTo(node.data) == 0)
            return node.data;
        if (target.compareTo(node.data) < 0)
            return find(target, node.left);
        return find(target, node.right);
    }

    public E find(E target) {
        return find(target, root);
    }

    public E delete(E target) {
        root = delete(target, root);
        return deletedData;
    }

    public E findIterative(E target) {
        var current = root;
        for (int diff = target.compareTo(current.data); diff != 0; diff = target.compareTo(current.data)) {
            if (diff < 0) {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current == null) {
                break;
            }
        }
        return current == null ? null : current.data;
    }

    public E maxIt() {
        var current = root;
        while (current != null && current.right != null) {
            current = current.right;
        }
        return current == null ? null : current.data;
    }

    public E maxRec() {
        return maxRec(root);
    }

    private E maxRec(Node<E> node) {
        return node.right == null ? node.data : maxRec(node.right);
    }

    private Node<E> delete(E target, Node<E> node) {
        if (node == null) {//target finns ej i trädet
            deletedData = null;
            return null;
        } else {
            if (target.compareTo(node.data) < 0) {//target finns i vänstra trädet
                node.left = delete(target, node.left); //om det finns
                return node;
            } else if (target.compareTo(node.data) > 0) {//target i högra trädet
                node.right = delete(target, node.right);
                return node;
            } else {//target finns i node!
                deletedData = node.data; //lagrar data att returnera

                //nu ska vi bygga om trädet
                if (node.left == null) //noden som ska bort saknar vänster träd
                    return node.right;
                else if (node.right == null)//noden som ska bort saknar högerträd
                    return node.left;
                else {//noden vi ska ta bort har två barn
                    Node<E> nodeToMove = node.right, parentNodeToMove = node;
                    if (nodeToMove.left == null) {//högra barnet har inget vänsterbarn
                        nodeToMove.left = node.left;
                        return nodeToMove;
                    }

                    //högra barnet har vänsterbarn
                    while (nodeToMove.left != null) {
                        parentNodeToMove = nodeToMove;
                        nodeToMove = nodeToMove.left;
                    }
                    parentNodeToMove.left = nodeToMove.right;
                    node.data = nodeToMove.data;
                    return node;
                }
            }
        }
    }

    public void printTree() {
        if (root == null) {
            System.out.println("Empty tree");
        }

        var printRows = new ArrayList<String>();
        buildPrintRows(root, 0, printRows);

        for (var row : printRows) {
            System.out.println(row);
        }
    }

    private void buildPrintRows(Node<E> node, int row, List<String> printRows) {
        if (row >= printRows.size()) {
            printRows.add(node == null ? "null" : node.data.toString());
        } else {
            printRows.set(row, printRows.get(row) + " " + (node == null ? "null" : node.data.toString()));
        }
        if (node == null) {
            return;
        }
        buildPrintRows(node.left, row + 1, printRows);
        buildPrintRows(node.right, row + 1, printRows);
    }

    public int numberOfLeaves() {
        return numberOfLeaves(root);
    }

    private int numberOfLeaves(Node<E> node) {
        if (node.left == null && node.right == null) {
            return 1;
        }

        int total = 0;
        if (node.left != null) {
            total += numberOfLeaves(node.left);
        }
        if (node.right != null) {
            total += numberOfLeaves(node.right);
        }
        return total;
    }

    public int numberOfNodes() {
        return numberOfNodes(root);
    }

    private int numberOfNodes(Node<E> node) {
        return node == null ? 0 : 1 + numberOfNodes(node.left) + numberOfNodes(node.right);
    }

    public int height() {
        return height(root);
    }

    public int height(Node<E> node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }
}

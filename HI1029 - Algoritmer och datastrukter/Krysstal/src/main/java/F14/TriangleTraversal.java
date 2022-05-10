package F14;

import java.util.*;

public class TriangleTraversal {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String[] Colors = new String[]{
            ANSI_BLACK,
            ANSI_RED,
            ANSI_GREEN,
            ANSI_YELLOW,
            ANSI_BLUE,
            ANSI_PURPLE,
            ANSI_CYAN,
            ANSI_WHITE
    };

    public static class Node<E> {
        public E value;
        public Node<E> left, right;

        public Node(E value) {
            this.value = value;
        }

        public Node(E value, Node<E> left, Node<E> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + value + ") " + " -> (L:" + (left == null ? "n" : left.value) + ") (R:" + (right == null ? "n" : right.value) + ")";
        }
    }


    private Node<Integer>[] nodes;
    private int height;
    private static final Random random = new Random();
    private int noCalls = 0;

    public TriangleTraversal(int height) {
//        // n(n + 1) / 2

        this.height = 5;
        int noNodes = height * (height + 1) / 2;
        nodes = new Node[noNodes];


        // Bottom row
        for (int i = nodes.length - height; i < nodes.length; i++) {
            nodes[i] = new Node<>(rInt());
        }

        int rowCounter = 0;
        int rowSize = height - 1;
        for (int i = nodes.length - height - 1; i >= 0; i--) {
            nodes[i] = new Node<>(rInt(), nodes[i + rowSize], nodes[i + rowSize + 1]);
            if (++rowCounter == rowSize) {
                rowCounter = 0;
                rowSize--;
            }
        }
    }

    public int solveRecursive() {
        noCalls = 0;
        return solveRecursive(nodes[0]);
    }

    public int solveRecursive(Node<Integer> node) {
        noCalls++;
        if (node == null) {
            return 0;
        }

        int tryLeft = solveRecursive(node.left);
        int tryRight = solveRecursive(node.right);
        return node.value + Math.max(tryLeft, tryRight);
    }

    public int solveDynamicTopDown() {
        noCalls = 0;
        var hashMap = new HashMap<Node<Integer>, Integer>();
        hashMap.put(nodes[0], nodes[0].value);
        return solveDynamicTopDown(nodes[0], hashMap);
    }

    public int solveDynamicTopDown(Node<Integer> node, HashMap<Node<Integer>, Integer> hashmap) {
        noCalls++;
        if (node == null) {
            return 0;
        }
        int tryLeft = tryIfBetterOrUndiscovered(node.left, node, hashmap);
        int tryRight = tryIfBetterOrUndiscovered(node.right, node, hashmap);
        return node.value + Math.max(tryLeft, tryRight);
    }

    private int tryIfBetterOrUndiscovered(Node<Integer> node, Node<Integer> from, HashMap<Node<Integer>, Integer> hashmap) {
        int result = 0;
        if (node == null) {
            return result;
        }

        int candidate = hashmap.get(from) + node.value;

        // Om tidigare hittat
        if (hashmap.containsKey(node)) {
            // Kolla om detta är en bättre väg
            if (candidate > hashmap.get(node)) {
                hashmap.put(node, candidate);
                result = solveDynamicTopDown(node, hashmap);
            }
        } else {
            hashmap.put(node, candidate);
            result = solveDynamicTopDown(node, hashmap);
        }
        return result;
    }

    public int solveDynamicBottomUp() {
        noCalls = 0;
        return solveDynamicBottomUp(new HashMap<>());
    }

    private int solveDynamicBottomUp(HashMap<Node<Integer>, Integer> hashMap) {
        noCalls++;
        // Starta från raden längst ner, gå baklänges
        int rowCounter = 0;
        int rowSize = height;
        for (int i = nodes.length - 1; i >= 0; i--) {
            var node = nodes[i];

            // Bottenraden
            if (node.left == null || node.right == null) {
                hashMap.put(node, node.value);
                continue;
            }

            var leftValue = hashMap.get(node.left);
            var rightValue = hashMap.get(node.right);

            hashMap.put(node, node.value + Math.max(leftValue, rightValue));

            if (++rowCounter == rowSize) {
                rowCounter = 0;
                rowSize--;
            }
        }
        return hashMap.get(nodes[0]);
    }

    private static int rInt() {
        return random.nextInt(89) + 10;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        int rowSize = 1;
        int rowCounter = 0;
        builder.append(Colors[0]);
        for (Node<Integer> node : nodes) {
            builder.append(node.value).append(" ");

            if (++rowCounter == rowSize) {
                rowSize++;
                rowCounter = 0;
                builder.append("\n").append(Colors[rowSize % Colors.length]);
            }
        }
        return builder.toString();
    }

    public String toStringNodes() {
        var builder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            builder.append(Colors[i % Colors.length]).append(nodes[i]).append("\n");
        }
        return builder.toString();
    }

    public int getNoCalls() {
        return noCalls;
    }
}

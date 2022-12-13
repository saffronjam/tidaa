package F11;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class GraphSolver {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public class Node implements Comparable<Node> {
        private final String name;
        private Node via = null;
        private final HashMap<Node, Float> neighbors = new HashMap<>();
        private float totalCost = Float.MAX_VALUE;

        public Node(String name) {
            this.name = name;
        }

        public void addNeighbor(Node node, float cost) {
            neighbors.put(node, cost);
        }

        public String getName() {
            return name;
        }

        public float getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(float value) {
            totalCost = value;
        }

        public void setVia(Node via) {
            this.via = via;
        }

        public Node getVia() {
            return via;
        }

        public HashMap<Node, Float> getNeighbors() {
            return neighbors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return name.equals(((Node) o).name);
        }

        @Override
        public int compareTo(Node node) {
            return name.compareTo(node.name);
        }
    }

    private final HashMap<String, Node> nodes = new HashMap<>();

    public GraphSolver(String filename) {
        try {
            File myObj = new File("src/main/java/F11/" + filename);
            Scanner reader = new Scanner(myObj);

            if (reader.hasNextLine()) {
                var allNodesAsString = reader.nextLine();
                var allNodesAsStringSeparated = allNodesAsString.split("\\W+");
                for (var name : allNodesAsStringSeparated) {
                    nodes.put(name, new Node(name));
                }
                while (reader.hasNextLine()) {
                    String data = reader.nextLine();
                    String[] separated = data.split("\\W+");
                    for (int i = 1; i < separated.length; i += 2) {
                        var first = nodes.get(separated[0]);
                        var second = nodes.get(separated[1]);
                        var cost = Integer.parseInt(separated[2]);

                        first.addNeighbor(second, cost);
                        second.addNeighbor(first, cost);
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public Deque<Node> solve(String fromName, String toName) {
        return solve(nodes.get(fromName), nodes.get(toName));
    }

    private Deque<Node> solve(Node from, Node to) {
        var unvisited = new ArrayList<>(nodes.values());
        from.totalCost = 0.0f;
        var next = from;
        while (next != null) {
            for (var entry : next.getNeighbors().entrySet()) {
                var neighbor = entry.getKey();
                var neighborCost = entry.getValue();

                // Om denna vägen är bättre, så ska vi ta denna vägen
                float suggestedNewCost = next.getTotalCost() + neighborCost;
                if (suggestedNewCost < neighbor.getTotalCost()) {
                    neighbor.setTotalCost(suggestedNewCost);
                    neighbor.setVia(next);
                }
            }
            unvisited.remove(next);
            // Hämta nästa nod med minst node.totalCost
            next = next(unvisited);
        }

        // Trace back path
        var path = new ArrayDeque<Node>();
        for (Node node = to; node != null; node = node.via) {
            path.addFirst(node);
        }
        return path;
    }

    private Node next(List<Node> unvisisted) {
        var result = unvisisted.stream().min(((Node left, Node right) -> (int) (left.getTotalCost() - right.getTotalCost())));
        return result.orElse(null);
    }

    public String toStringNodes() {
        var builder = new StringBuilder();
        for (var node : nodes.values()) {
            builder.append(ANSI_PURPLE)
                    .append(node.getName())
                    .append(ANSI_WHITE)
                    .append(" (")
                    .append(ANSI_GREEN)
                    .append(node.getNeighbors().size())
                    .append(ANSI_WHITE)
                    .append(" neighbors)")
                    .append(ANSI_RESET)
                    .append(" : ");
            for (var entry : node.getNeighbors().entrySet()) {
                builder.append(entry.getKey().getName()).append(":").append(entry.getValue()).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String toStringPath(Deque<Node> path) {
        var builder = new StringBuilder();
        int index = 0;
        for (var node : path) {
            builder.append(ANSI_RED).append(node.getName()).append(ANSI_WHITE).append("(").append(node.getTotalCost()).append(")");
            if (index++ < path.size() - 1) {
                builder.append(ANSI_RESET).append(" --> ");
            }
        }
        builder.append(ANSI_YELLOW)
                .append("   Cost: ")
                .append(path.getLast().getTotalCost())
                .append(ANSI_RESET);
        return builder.toString();
    }

}

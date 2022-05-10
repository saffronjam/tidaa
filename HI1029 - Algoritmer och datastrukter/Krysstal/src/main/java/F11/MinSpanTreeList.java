package F11;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MinSpanTreeList {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static class Node implements Comparable<Node> {
        private final String name;
        private Node via = null;
        private final HashMap<Node, Integer> neighbors = new HashMap<>();
        private int cost = Integer.MAX_VALUE;

        public Node(String name) {
            this.name = name;
        }

        public void addNeighbor(Node node, int cost) {
            neighbors.put(node, cost);
        }

        public String getName() {
            return name;
        }

        public void setVia(Node via) {
            this.via = via;
        }

        public Node getVia() {
            return via;
        }

        public HashMap<Node, Integer> getNeighbors() {
            return neighbors;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
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

        @Override
        public String toString() {
            return "[" + name + "]";
        }
    }

    private final HashMap<String, Node> nodes = new HashMap<>();

    public MinSpanTreeList(String filename) {
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

    public void solve() {
        var unvisited = new ArrayList<>(nodes.values());
        var next = nodes.get("A");
        while (next != null) {
            unvisited.remove(next);

            for (var entry : next.getNeighbors().entrySet()) {
                var neighbor = entry.getKey();
                // Lägg bara till grannen i kön om den inte blivit utforskad ännu.
                if (unvisited.contains(neighbor)) {
                    var neighborCost = entry.getValue();
                    // Uppdatera även cost ifall detta är en bättre väg
                    if (neighborCost < neighbor.getCost()) {
                        neighbor.setCost(neighborCost);
                        neighbor.setVia(next);
                    }
                }
            }

            System.out.println(Arrays.toString(unvisited.toArray()));
            // Hämta nästa node som har lägst node.cost
            next = next(unvisited);
        }
    }

    private Node next(List<Node> unvisisted) {
        var result = unvisisted.stream().min(((Node left, Node right) -> (int) (left.getCost() - right.getCost())));
        return result.orElse(null);
    }


    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var node : nodes.values()) {
            if (node.getVia() == null) continue;

            builder.append(ANSI_RED)
                    .append(node.getName())
                    .append(ANSI_WHITE)
                    .append(" connected via ")
                    .append(ANSI_GREEN)
                    .append(node.via.getName())
                    .append("\n");
        }
        builder.append(ANSI_RESET);
        return builder.toString();
    }

}

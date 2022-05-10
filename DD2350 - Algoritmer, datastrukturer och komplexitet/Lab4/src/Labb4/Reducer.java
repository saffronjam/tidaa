package Labb4;

import java.io.*;
import java.util.ArrayList;

public class Reducer {
    private static boolean useStdIn = true;
    Kattio io;

    public static class Node {
        public int from, to;

        public Node(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    // Graph color data
    int v;
    int e;
    int colors;
    Node[] nodes;
    int[] countEdges;

    // Role assignment data
    int roles;
    int scenes;
    int actors;
    boolean[] hasEdges;
    int hasEdgeCounter;


    private void reduceToRoleAssignment() {
        roles = v + 2;
        scenes = e + 2;
        actors = colors + 2;
        io.println(v + 2);
        io.println(e + 2 + hasEdges.length - hasEdgeCounter);

        colors = Math.min(colors, v);
        io.println(colors + 2);

        // The <div/>'s
        io.println("1 1");
        io.println("1 2");
        StringBuilder builder;
        for (int i = 2; i < roles; i++) {
            if (hasEdges[i - 2]) {

                builder = new StringBuilder();
                builder.append(colors);
                for (int j = 0; j < colors; j++) {
                    builder.append(' ').append(j + 1 + 2);
                }
                io.println(builder);
            } else {
                io.println("1 3");
            }
        }

        io.println("2 1 3");
        io.println("2 2 3");
        for (int i = 0; i < scenes - 2; i++) {
            builder = new StringBuilder();
            io.println(builder.append("2 ").append(nodes[i].from + 2).append(' ').append(nodes[i].to + 2));
        }

        // Fix empty nodes
        int i = 2;
        for (var hasEdge : hasEdges) {
            builder = new StringBuilder();

            i++;
            if (!hasEdge) {
                io.println(builder.append("2 1 ").append(i));
            }

        }
    }

    private void readGraphColorGraph() {
        v = io.getInt();
        e = io.getInt();
        colors = io.getInt();

        countEdges = new int[v];
        hasEdges = new boolean[v];
        hasEdgeCounter = 0;
        nodes = new Node[e];
        for (int i = 0; i < e; i++) {
            int from = io.getInt();
            int to = io.getInt();
            nodes[i] = new Node(from, to);

            if (!hasEdges[from - 1]) {
                hasEdges[from - 1] = true;
                hasEdgeCounter++;
            }
            countEdges[from - 1]++;

            if (!hasEdges[to - 1]) {
                hasEdges[to - 1] = true;
                hasEdgeCounter++;
            }
            countEdges[to - 1]++;
        }
    }

    public Reducer() {
        if (useStdIn) {
            io = new Kattio(System.in, System.out);
        } else {
            var path = "res/indata.txt";
            var output = "res/output.txt";
            try {
                io = new Kattio(new BufferedInputStream(new FileInputStream(path)), new FileOutputStream(output));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        readGraphColorGraph();
        reduceToRoleAssignment();

        io.flush();
    }

    public static void main(String[] args) {
        new Reducer();


    }
}
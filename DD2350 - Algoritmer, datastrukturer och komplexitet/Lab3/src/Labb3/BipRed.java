package Labb3;

import java.io.*;
import java.util.*;

/**
 * Exempel på in- och utdatahantering för maxflödeslabben i kursen
 * ADK.
 * <p>
 * Använder Kattio.java för in- och utläsning.
 * Se http://kattis.csc.kth.se/doc/javaio
 *
 * @author: Per Austrin
 */

public class BipRed {
    Kattio io;

    ArrayList<Integer>[] capacityList;

    int totalConnetions = 0;
    int leftSize, rightSize;

    public static class Pair {
        public int from, to, value;

        public Pair(int from, int to) {
            this.from = from;
            this.to = to;
            this.value = 0;
        }

        public Pair(int from, int to, int value) {
            this.from = from;
            this.to = to;
            this.value = value;
        }
    }

    public static class Graph {
        public ArrayList<Pair> pairs;
        public int v, s, t, e, totFlow;
    }

    ArrayList<Pair> matches = new ArrayList<>();
    int maxMatch;

    void readBipartiteGraph() {
        // Läs antal hörn och kanter
        int x = io.getInt();
        int y = io.getInt();
        int e = io.getInt();

        leftSize = x;
        rightSize = y;
        capacityList = new ArrayList[x + y + 2];

        // Läs in kanterna
        for (int i = 0; i < e; ++i) {
            int a = io.getInt() - 1;
            int b = io.getInt() - 1;
            if (capacityList[a] == null) {
                capacityList[a] = new ArrayList<>();
            }
            capacityList[a].add(b);
        }
        totalConnetions += e;

        // Sätt att källa kopplas till alla noder i x
        capacityList[capacityList.length - 2] = new ArrayList<>(x);
        for (int i = 0; i < x; i++) {
            capacityList[capacityList.length - 2].add(i);
        }
        totalConnetions += x;

        // Sätt att sänka kopplas från alla noder i y
        capacityList[capacityList.length - 1] = new ArrayList<>(y);
        for (int i = x; i < x + y; i++) {
            capacityList[capacityList.length - 1].add(i);
        }
        totalConnetions += y;
    }


    Graph translateToMaxFlow() {
        int v = capacityList.length, e = totalConnetions, s = capacityList.length - 1, t = capacityList.length;

        var graph = new Graph();

        graph.v = v;
        graph.s = s;
        graph.t = t;
        graph.e = e;
        graph.pairs = new ArrayList<>(e);
        // Skriv ut antal hörn och kanter samt källa och sänka
        for (int i = 0; i < leftSize; ++i) {

            if (capacityList[i] != null) {
                for (int j = 0; j < capacityList[i].size(); j++) {
                    int a = i;
                    int b = capacityList[i].get(j);
                    // Kant från a till b med kapacitet c
                    var pair = new Pair((a + 1), (b + 1), 1);
                    graph.pairs.add(pair);
                }
            }
        }

        for (int i = 0; i < leftSize; i++) {
            var pair = new Pair(capacityList.length - 1, i + 1, 1);
            graph.pairs.add(pair);
        }

        for (int i = leftSize; i < leftSize + rightSize; i++) {
            var pair = new Pair(i + 1, capacityList.length, 1);
            graph.pairs.add(pair);
        }


        io.flush();
        System.err.println("Skickade iväg flödesgrafen");
        return graph;
    }


    void translateToBipartite(Graph graph) {
        int totflow = graph.totFlow;
        int e = graph.e;

        maxMatch = totflow;
        matches = new ArrayList<>(e);

        for (var pair : graph.pairs) {
            // Vi lägger inte till start och slut noden för bipartit-lösningen
            if (pair.from >= capacityList.length - 1 || pair.to >= capacityList.length - 1)
                break;
            matches.add(pair);
        }
    }


    void writeBipMatchSolution() {
        // Skriv ut antal hörn och storleken på matchningen
        io.println(leftSize + " " + rightSize);
        io.println(maxMatch);

        for (var match : matches) {
            int a = match.from, b = match.to;
            // Kant mellan a och b ingår i vår matchningslösning
            io.println(a + " " + b);
        }
        io.flush();
    }

    public BipRed() throws IOException {
        boolean useStdIn = true;

        if (useStdIn) {
            io = new Kattio(System.in, System.out);
        } else {
            var path = "src/input/maffigttest.indata";
            io = new Kattio(new BufferedInputStream(new FileInputStream(path)), System.out);
        }

        // Läs indata
        readBipartiteGraph();

        // Reduera till maximala flödesproblemet
        var flow = translateToMaxFlow();
        var maxFlowSolver = new MaxFlow(flow);
        var maxFlowGraph = maxFlowSolver.getFlow();
        translateToBipartite(maxFlowGraph);

        // Skriv ut resultatet
        writeBipMatchSolution();

        io.close();
    }

    public static void main(String args[]) throws IOException {
        new BipRed();
    }
}


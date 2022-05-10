package Labb3;

import java.util.*;

import Labb3.BipRed.*;

public class MaxFlow {
    private int startIndex;
    private int endIndex;

    private HashMap<Integer, Integer>[] flows;
    private HashMap<Integer, Integer>[] capacities;
    private HashMap<Integer, Integer>[] restCapacities;

    // Results
    private int maxFlow;

    public MaxFlow(Graph graph) {
        readGraph(graph);
        calculateMaxFlow();
    }

    private void readGraph(Graph graph) {
        int v = graph.v;

        int s = graph.s - 1;
        int t = graph.t - 1;
        int e = graph.e;

        startIndex = s;
        endIndex = t;

        flows = new HashMap[v];
        capacities = new HashMap[v];
        restCapacities = new HashMap[v];

        for (int i = 0; i < v; i++) {
            flows[i] = new HashMap<>();
            capacities[i] = new HashMap<>();
            restCapacities[i] = new HashMap<>();
        }

        for (int i = 0; i < e; i++) {
            var pair = graph.pairs.get(i);
            int a = pair.from - 1;
            int b = pair.to - 1;
            int c = pair.value;

            flows[a].put(b, 0);
            flows[b].put(a, 0);
            capacities[a].put(b, c);
            restCapacities[a].put(b, c);
            if (!restCapacities[b].containsKey(a)) {
                restCapacities[b].put(a, 0);
            }
        }
    }

    public Graph getFlow() {
        var graph = new Graph();

        var entryList = getEdges();

        graph.v = flows.length;
        graph.s = startIndex + 1;
        graph.t = endIndex + 1;
        graph.totFlow = maxFlow;
        graph.e = entryList.size();

        graph.pairs = new ArrayList<>(graph.e);

        for (var pair : entryList) {
            int from = pair.getKey();
            int to = pair.getValue();
            var graphPair = new Pair(from + 1, to + 1, flows[from].get(to));
            graph.pairs.add(graphPair);
        }

        return graph;
    }

    private void calculateMaxFlow() {
        int totalFlow = 0;
        for (var path = getBfsPath(); !path.isEmpty(); path = getBfsPath()) {
            var maxAlteration = getMaxRestCapacity(path);
            alterPath(path, maxAlteration);
            totalFlow += maxAlteration;
        }
        maxFlow = totalFlow;
    }

    private ArrayList<Integer> getBfsPath() {
        var visisted = new boolean[restCapacities.length];
        var queue = new ArrayDeque<Integer>();
        var parents = new int[restCapacities.length];
        Arrays.fill(parents, -1);

        visisted[startIndex] = true;
        queue.add(startIndex);

        while (!queue.isEmpty()) {
            int next = queue.poll();

            for (var rest : restCapacities[next].entrySet()) {
                int to = rest.getKey();
                int value = rest.getValue();

                if (visisted[to] || value <= 0) {
                    continue;
                }

                if (to == endIndex) {
                    parents[to] = next;
                    return makePathFromParents(parents);
                }

                queue.add(to);
                parents[to] = next;
                visisted[to] = true;
            }
        }

        return new ArrayList<>();
    }

    private ArrayList<Integer> makePathFromParents(int[] parents) {
        var path = new ArrayList<Integer>();
        int current = endIndex;
        while (current != startIndex) {
            path.add(current);
            current = parents[current];
        }
        path.add(startIndex);
        Collections.reverse(path);

        return path;
    }

    private int getMaxRestCapacity(List<Integer> path) { // find bottle-neck in path
        int min = Integer.MAX_VALUE;
        for (int i = 1; i < path.size(); i++) {
            var list = restCapacities[path.get(i - 1)];
            min = Math.min(min, list.get(path.get(i)));
        }
        return min;
    }

    private void alterPath(List<Integer> path, int value) {
        for (int i = 1; i < path.size(); i++) {
            int from = path.get(i - 1);
            int to = path.get(i);

            flows[from].put(to, flows[from].get(to) + value);
            flows[to].put(from, -flows[from].get(to));

            restCapacities[from].put(to, restCapacities[from].get(to) - value);
            restCapacities[to].put(from, restCapacities[to].get(from) + value);
        }
    }

    private ArrayList<Map.Entry<Integer, Integer>> getEdges() {
        var entryList = new ArrayList<Map.Entry<Integer, Integer>>();

        for (int i = 0; i < flows.length; i++) {
            for (var entry : flows[i].entrySet()) {
                int other = entry.getKey();
                if (capacities[i].containsKey(other)) {
                    if (flows[i].get(other) > 0) {
                        entryList.add(new AbstractMap.SimpleEntry<>(i, other));
                    }
                }
            }
        }
        entryList.sort((a, b) -> {
            if (a.getKey().equals(b.getKey())) {
                return a.getValue() - b.getValue();
            }
            return a.getKey() - b.getKey();
        });

        return entryList;
    }


    public static void main(String[] args) {
        new MaxFlow(null);
    }
}

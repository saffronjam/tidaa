package F11;

public class TesterGraphSolver {
    public static void main(String[] args) {
        var graphSolver = new GraphSolver("nodes.txt");
        System.out.println(graphSolver.toStringNodes());
        var path = graphSolver.solve("A", "H");
        System.out.println(graphSolver.toStringPath(path));

    }
}

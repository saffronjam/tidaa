package F11;

public class TesterMinSpanTreeList {
    public static void main(String[] args) {
        var minSpanTreeList = new MinSpanTreeList("nodes.txt");
        minSpanTreeList.solve();
        System.out.println(minSpanTreeList);

    }
}

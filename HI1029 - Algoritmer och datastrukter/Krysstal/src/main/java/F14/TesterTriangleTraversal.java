package F14;

public class TesterTriangleTraversal {


    public static void main(String[] args) {
        var myTriangleTraversal = new TriangleTraversal(10);

        System.out.println(myTriangleTraversal);
        System.out.println(myTriangleTraversal.toStringNodes());

        {
            var begin = System.nanoTime();
            var result = myTriangleTraversal.solveRecursive();
            var end = System.nanoTime();
            var noCalls = myTriangleTraversal.getNoCalls();
            System.out.println("Solution recursive: " + result + " (Calls: " + noCalls + ") (Took: " + (end - begin) / 1000 + " us)");
        }
        {
            var begin = System.nanoTime();
            var result = myTriangleTraversal.solveDynamicTopDown();
            var end = System.nanoTime();
            var noCalls = myTriangleTraversal.getNoCalls();
            System.out.println("Solution dynamic top-down: " + result + " (Calls: " + noCalls + ")  (Took: " + (end - begin) / 1000 + " us)");
        }
        {
            var begin = System.nanoTime();
            var result = myTriangleTraversal.solveDynamicBottomUp();
            var end = System.nanoTime();
            var noCalls = myTriangleTraversal.getNoCalls();
            System.out.println("Solution dynamic bottom-up: " + result + " (Calls: " + noCalls + ")  (Took: " + (end - begin) / 1000 + " us)");
        }
    }
}



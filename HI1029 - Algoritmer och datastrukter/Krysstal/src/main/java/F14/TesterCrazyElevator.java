package F14;

public class TesterCrazyElevator {
    public static void main(String[] args) {
        var myCrazyElevator = new CrazyElevator(7, 5, 3, 45, 30);

        int destinationFloor = 35;
        {
            var begin = System.nanoTime();
            var result = myCrazyElevator.takeMeTo(destinationFloor, CrazyElevator.Strategy.DynamicTopDown);
            var end = System.nanoTime();
            var buttonCount = myCrazyElevator.getButtonCount();
            System.out.println("Solution dynamic top-down: " + result + " buttons (Pressed: " + buttonCount + ") (Took: " + (end - begin) / 1000 + " us)");
        }
        {
            var begin = System.nanoTime();
            var result = myCrazyElevator.takeMeTo(destinationFloor, CrazyElevator.Strategy.NativeRecursion);
            var end = System.nanoTime();
            var buttonCount = myCrazyElevator.getButtonCount();
            System.out.println("Solution recursive: " + result + " buttons (Pressed: " + buttonCount + ") (Took: " + (end - begin) / 1000 + " us)");
        }
    }
}

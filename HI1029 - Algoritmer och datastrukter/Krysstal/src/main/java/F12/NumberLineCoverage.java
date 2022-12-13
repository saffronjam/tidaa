package F12;

import java.util.Random;

public class NumberLineCoverage {

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

    private final int intervalLength = 2;
    private StringBuilder resultBuilder;

    public int coverUp(float[] points) {
        assert points.length > 0;
        resultBuilder = new StringBuilder();

        int noIntervals = 0;
        for (int i = 0; i < points.length; i++) {
            int startMeasureIndex = i;

            var randomColor = Colors[i % Colors.length];
            resultBuilder.append(" ").append(randomColor);
            while (i < points.length - 1 && points[i + 1] - points[startMeasureIndex] <= intervalLength) {
                resultBuilder.append(points[i]).append(" ");
                i++;
            }
            resultBuilder.append(points[i]).append(" ").append(" ").append(ANSI_RESET);

            noIntervals++;
        }

        return noIntervals;

        // Resonemang:
        // Vi fortsätter traversera frammåt i arrayen sålänge vårt intervall
        // täcker nästa tal, från vår bas (startMeasureIndex)
        // När vi inte längre täcker över nästa tal, ökar vi antal intervall
        // och sätter startMeasureIndex till current
    }

    private static String randomANSIColor() {
        return Colors[new Random().nextInt(Colors.length)];
    }

    @Override
    public String toString() {
        return resultBuilder.toString();
    }
}

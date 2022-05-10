package F12;

public class Activity implements Comparable<Activity> {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public final String name;
    public final Date start;
    public final Date end;

    public Activity(String name, Date start, Date end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return ANSI_BLUE +
                name +
                ANSI_GREEN +
                " Start: " +
                ANSI_RESET +
                "[" +
                ANSI_PURPLE +
                dateToString(start.date) +
                " " +
                ANSI_CYAN +
                start.hour +
                ":" +
                "00" +
                ANSI_RESET +
                "]" +
                ANSI_YELLOW +
                " ==> " +
                ANSI_RED +
                "End: " +
                ANSI_RESET +
                "[" +
                ANSI_PURPLE +
                dateToString(end.date) +
                " " +
                ANSI_CYAN +
                end.hour +
                ":" +
                "00" +
                ANSI_RESET +
                "]";
    }

    private String dateToString(int date) {
        return switch (date) {
            case 0 -> "Mon";
            case 1 -> "Tue";
            case 2 -> "Wed";
            case 3 -> "Thu";
            case 4 -> "Fri";
            default -> "Unknown";
        };
    }

    public boolean overlapsWith(Activity other) {
        return end.compareTo(other.start) > 0 && start.compareTo(other.end) < 0;
    }

    @Override
    public int compareTo(Activity other) {
        var endCompare = end.compareTo(other.end);
        return endCompare == 0 ? start.compareTo(other.start) : endCompare;
    }
}

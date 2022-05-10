public class Printer {
    private static boolean enabled;

    public Printer(boolean enabled) {
        this.enabled = enabled;
    }


    public static void setEnabled(boolean set) {
        enabled = set;
    }


    public static void write(String text) {
        if (enabled) {
            System.out.println(text);
        }
    }
}

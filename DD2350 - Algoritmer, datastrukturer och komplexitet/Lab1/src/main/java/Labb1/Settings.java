package Labb1;

public class Settings {
    private static final String Size = "big";

    public static String ResourcePath = "C:\\Users\\ownem\\source\\repos\\Labb1ADKJava\\src\\main\\resources\\";
    public static String KorpusPath = ResourcePath + "korpus_" + Size;
    public static String IndexPath = ResourcePath + "index_" + Size + ".txt";
    public static String CounterPath = ResourcePath + "wordData_" + Size + ".txt";
    public static String PrehashWordData = ResourcePath + "prehashCounter_" + Size + ".bin";
}

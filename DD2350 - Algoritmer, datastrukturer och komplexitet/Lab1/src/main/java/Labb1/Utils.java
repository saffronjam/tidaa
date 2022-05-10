package Labb1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

public class Utils {

    static private int offset;

    static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static char getChar(BufferedReader in) {
        char tkn = '\000';
        try {
            tkn = (char) in.read();
            offset++;
        } catch (IOException e) {
            System.err.println("Fel i GetChar");
        }
        return tkn;
    }

    public static char peekChar(BufferedReader in) throws IOException {
        char tkn = '\000';
        in.mark(1);
        try {
            tkn = (char) in.read();
            in.reset();
        } catch (IOException e) {
            System.err.println("Fel i NextChar\n");
        }
        return tkn;
    }

    public static void skipBlanks(BufferedReader in) throws IOException {
        while (isWhitespace(peekChar(in))) {
            getChar(in);
        }
    }

    public static void skipUntilNextLine(BufferedReader in) throws IOException {
        while (peekChar(in) != '\n') {
            getChar(in);
        }
        skipBlanks(in);
    }

    public static String getWord(BufferedReader in) throws IOException {
        StringBuilder word = new StringBuilder();
        skipBlanks(in);
        if (isEof(in)) return "";
        while (!isWhitespace(peekChar(in))) word.append(getChar(in));
        return word.toString();
    }

    public static int getInt(BufferedReader in) throws IOException {
        String word = getWord(in);
        try {
            return Integer.parseInt(word);
        } catch (NumberFormatException e) {
            System.err.println("Not an integer:" + word);
        }
        return 0;
    }

    public static int getBinInt(DataInputStream in) throws IOException {
        int tkn = 0;
        try {
            tkn = in.readInt();
        } catch (IOException e) {
            System.err.println("Fel i GetChar");
        }
        return tkn;
    }

    public static boolean isEof(BufferedReader in) throws IOException {
        int n = 0;
        in.mark(1);
        try {
            n = in.read();
            in.reset();
        } catch (IOException e) {
            System.err.println("Fel i EOF\n");
        }
        return (n == -1);
    }


    public static String fillWithSpaces(String input) {
        StringBuilder sb = new StringBuilder(input);
        while (sb.length() < 3) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static int lazyManHash(String lowerCaseString) {

        return (convertToInt(lowerCaseString.charAt(0)) * 900) +
                (convertToInt(lowerCaseString.charAt(1)) * 30) +
                convertToInt(lowerCaseString.charAt(2));
    }

    public static int convertToInt(char ch) {
        switch (ch) {
            case ' ':
                return 0;
            case 'å':
                return 27;
            case 'ä':
                return 28;
            case 'ö':
                return 29;
            default:
                return ch - 'a' + 1;
        }
    }

    public static void resetOffset() {
        offset = 0;
    }

    public static int getOffset() {
        return offset;
    }

}

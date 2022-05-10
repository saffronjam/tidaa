package Tenta160234.uppgift3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Maze {

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

    private enum Coord {
        Start, Goal, Wall, Open, Visited, Correct
    }

    int rows, columns;
    Coord[][] mazeMatrix;

    int startRow, startColumn;
    int goalRow, goalColumn;

    public Maze() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/java/Tenta160234/uppgift3/Labyrint.txt"));
            rows = Integer.parseInt(in.readLine());
            columns = Integer.parseInt(in.readLine());
            mazeMatrix = new Coord[rows][columns];
            for (int j = 0; j < columns; j++) {
                mazeMatrix[0][j] = Coord.Wall;
                mazeMatrix[rows - 1][j] = Coord.Wall;
            }
            for (int i = 1; i < rows - 1; i++) {
                mazeMatrix[i][0] = Coord.Wall;
                mazeMatrix[i][columns - 1] = Coord.Wall;
            }
            for (int i = 0; i < rows; i++) {
                String s = in.readLine();
                for (int j = 0; j < columns; j++) {
                    mazeMatrix[i][j] = Coord.Open;
                    if (s.charAt(j) == '*')
                        mazeMatrix[i][j] = Coord.Wall;
                    else if (s.charAt(j) == 'g') {
                        mazeMatrix[i][j] = Coord.Goal;
                        goalRow = i;
                        goalColumn = j;
                    } else if (s.charAt(j) == 's') {
                        mazeMatrix[i][j] = Coord.Start;
                        startRow = i;
                        startColumn = j;
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public boolean solve() {
        return solve(startRow, startColumn);
    }

    public boolean solve(int row, int column) {
        if (!isValid(row, column) || isVisisted(row, column)) {
            return false;
        }
        if (isGoal(row, column)) {
            return true;
        }
        markVisisted(row, column);

        boolean result = solve(row, column - 1) ||
                solve(row - 1, column) ||
                solve(row, column + 1) ||
                solve(row + 1, column);

        if (result) {
            markCorrect(row, column);
        }
        return result;
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                switch (mazeMatrix[i][j]) {
                    case Correct -> System.out.print(ANSI_GREEN + " ██");
                    case Start -> System.out.print(ANSI_WHITE + " ██");
                    case Goal -> System.out.print(ANSI_BLACK + " ██");
                    case Wall -> System.out.print(ANSI_RED + " ██");
                    case Visited -> System.out.print(ANSI_BLUE + " ██");
                    case Open -> System.out.print(ANSI_PURPLE + " ██");
                }

            }
            System.out.println();
        }
    }

    private boolean isGoal(int row, int column) {
        return row == goalRow && column == goalColumn;
    }

    private boolean isValid(int row, int column) {
        return row >= 0 && row < rows &&
                column >= 0 && column < columns &&
                mazeMatrix[row][column] != Coord.Wall;
    }

    private boolean isVisisted(int row, int column) {
        return mazeMatrix[row][column] == Coord.Visited;
    }

    private void markVisisted(int row, int column) {
        mazeMatrix[row][column] = Coord.Visited;
    }

    private void markCorrect(int row, int column) {
        mazeMatrix[row][column] = Coord.Correct;
    }
}

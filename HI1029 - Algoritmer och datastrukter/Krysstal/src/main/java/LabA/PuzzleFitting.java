package LabA;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PuzzleFitting {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static class Piece {
        public int x1, y1, x2, y2, x3, y3;
        public int rotation;
        public String printString;

        public Piece(int x, int y, int rotation) {
            this.x1 = x;
            this.y1 = y;

            this.rotation = rotation;
            switch (rotation) {
                case 0 -> {
                    x2 = x;
                    y2 = y + 1;
                    x3 = x - 1;
                    y3 = y + 1;
                    printString = ANSI_BLUE;
                }
                case 1 -> {
                    x2 = x - 1;
                    y2 = y;
                    x3 = x - 1;
                    y3 = y - 1;
                    printString = ANSI_GREEN;
                }
                case 2 -> {
                    x2 = x;
                    y2 = y - 1;
                    x3 = x + 1;
                    y3 = y - 1;
                    printString = ANSI_RED;
                }
                case 3 -> {
                    x2 = x + 1;
                    y2 = y;
                    x3 = x + 1;
                    y3 = y + 1;
                    printString = ANSI_PURPLE;
                }
            }
        }
    }

    private static class Coord {
        public boolean state;
        public String colorString;

        public Coord(boolean state, String colorString) {
            this.state = state;
            this.colorString = colorString;
        }
    }

    private final Coord[][] board;
    private int noSolutions = 0;
    private final int solveCondition;

    public PuzzleFitting(int xGrey, int yGrey, int boardSize, int solveCondition) {
        board = new Coord[boardSize][boardSize];
        this.solveCondition = solveCondition;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = new Coord(false, "");
            }
        }
        board[yGrey][xGrey] = new Coord(true, ANSI_WHITE);
    }

    public void solve() {
        solve(0, 0, 0);
        System.out.println(noSolutions == 0 ? "No solutions found" : noSolutions + " solutions found");
    }

    private void solve(int placedPieces, int rowIn, int columnIn) {
        if (rowIn >= board.length) {
            return;
        }

        if (placedPieces == solveCondition) {
            noSolutions++;
            System.out.println(this);
            return;
        }

        for (int column = columnIn; column < board[rowIn].length; column++) {
            for (int rotation = 0; rotation < 4; rotation++) {
                var piece = new Piece(column, rowIn, rotation);
                if (canPlace(piece)) {
                    place(piece);
                    solve(placedPieces + 1, rowIn, column + 1);
                    unplace(piece);
                }
            }
        }
        if (isFullRow(rowIn)) {
            solve(placedPieces, rowIn + 1, 0);
        }
    }

    /*
    ..........   *             *  *
        *    ->  *  *    ->    *    ->    *  *
     *  *                                    *
     */

    /*
     * @param rotation: number of 90 degrees rotations clockwise
     */
    private boolean canPlace(Piece piece) {
        return isValidPlace(piece.x1, piece.y1) && isValidPlace(piece.x2, piece.y2) && isValidPlace(piece.x3, piece.y3);
    }

    private boolean isValidPlace(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board.length && !board[x][y].state;
    }

    private boolean isFullRow(int row) {
        boolean full = true;
        for (int i = 0; i < row; i++) {
            if (!board[row][i].state) {
                full = false;
                break;
            }
        }
        return full;
    }

    private void place(Piece piece) {
        setBoardState(piece.x1, piece.y1, new Coord(true, piece.printString));
        setBoardState(piece.x2, piece.y2, new Coord(true, piece.printString));
        setBoardState(piece.x3, piece.y3, new Coord(true, piece.printString));
    }

    private void unplace(Piece piece) {
        setBoardState(piece.x1, piece.y1, new Coord(false, piece.printString));
        setBoardState(piece.x2, piece.y2, new Coord(false, piece.printString));
        setBoardState(piece.x3, piece.y3, new Coord(false, piece.printString));
    }

    private void setBoardState(int x, int y, Coord coord) {
        board[x][y] = coord;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var row : board) {
            for (var coord : row) {
                builder.append(coord.colorString).append(coord.state ? "██ " : "   ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}

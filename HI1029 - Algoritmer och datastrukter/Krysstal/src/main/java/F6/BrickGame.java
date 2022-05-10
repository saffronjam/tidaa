package F6;

import java.util.*;

public class BrickGame {
    public enum Cell {
        CLEAR,
        GREY,
        WHITE
    }

    public class Move {
        int from, to;

        public Move(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[" + from + "=>" + to + ']';
        }
    }

    private final Stack<Move> goodMoves = new Stack<>();

    public static void main(String[] args) {

        var before = System.currentTimeMillis();

        BrickGame brickGame = new BrickGame();
        var result = brickGame.solveBoard(new Cell[]{Cell.GREY, Cell.GREY, Cell.GREY, Cell.CLEAR, Cell.WHITE, Cell.WHITE, Cell.WHITE});

        var board = new Cell[]{Cell.GREY, Cell.GREY, Cell.GREY, Cell.CLEAR, Cell.WHITE, Cell.WHITE, Cell.WHITE};
        System.out.println("\n\nSolved with " + brickGame.goodMoves.size() + " steps:");

        while (!brickGame.goodMoves.isEmpty()) {
            Move move = brickGame.goodMoves.pop();
            board[move.to] = board[move.from];
            board[move.from] = Cell.CLEAR;
            System.out.println("Moving " + move.from + " to " + move.to + ": " + boardToString(board));
        }

        var after = System.currentTimeMillis();

        System.out.println("Took: " + ((after - before) / 1000.0f) + " seconds");
    }

    public boolean solveBoard(Cell[] board) {
        int freeIndex = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == Cell.CLEAR) {
                freeIndex = i;
            }
        }
        return solveBoard(board, null, freeIndex, 0);
    }

    private boolean solveBoard(Cell[] board, Cell[] lastBoard, int freeIndex, int attempt) {
        if (isDone(board)) {
            return true;
        }
        if (attempt > 15) {
            return false;
        }

        if (tryMove(board, lastBoard, freeIndex - 2, freeIndex, attempt)) {
            goodMoves.push(new Move(freeIndex - 2, freeIndex));
            return true;
        }
        if (tryMove(board, lastBoard, freeIndex - 1, freeIndex, attempt)) {
            goodMoves.push(new Move(freeIndex - 1, freeIndex));
            return true;
        }
        if (tryMove(board, lastBoard, freeIndex + 1, freeIndex, attempt)) {
            goodMoves.push(new Move(freeIndex + 1, freeIndex));
            return true;
        }
        if (tryMove(board, lastBoard, freeIndex + 2, freeIndex, attempt)) {
            goodMoves.push(new Move(freeIndex + 2, freeIndex));
            return true;
        }
        return false;
    }

    private boolean tryMove(Cell[] board, Cell[] lastBoard, int from, int to, int attempt) {
        if (inBounds(board, from)) {
            var newBoard = Arrays.copyOf(board, board.length);
            newBoard[to] = newBoard[from];
            newBoard[from] = Cell.CLEAR;
            return !isSame(newBoard, lastBoard) && solveBoard(newBoard, board, from, attempt + 1);
        }
        return false;
    }

    private boolean inBounds(Cell[] board, int index) {
        return index >= 0 && index < board.length;
    }

    private boolean isDone(Cell[] board) {
        return board[0] == Cell.WHITE &&
                board[1] == Cell.WHITE &&
                board[2] == Cell.WHITE &&
                board[3] == Cell.CLEAR &&
                board[4] == Cell.GREY &&
                board[5] == Cell.GREY &&
                board[6] == Cell.GREY;
    }

    private boolean isSame(Cell[] lhs, Cell[] rhs) {
        if (lhs == null || rhs == null || lhs.length != rhs.length) {
            return false;
        }
        boolean isSame = true;
        for (int i = 0; i < lhs.length; i++) {
            if (lhs[i] != rhs[i]) {
                isSame = false;
                break;
            }
        }
        return isSame;
    }

    private static String boardToString(Cell[] board) {
        var builder = new StringBuilder("[");
        for (var cell : board) {
            switch (cell) {
                case CLEAR -> builder.append("-");
                case GREY -> builder.append("X");
                case WHITE -> builder.append("O");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}

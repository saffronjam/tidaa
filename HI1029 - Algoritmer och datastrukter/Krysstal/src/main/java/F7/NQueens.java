package F7;

public class NQueens {

    private int noSolutions = 0;

    public static void main(String[] args) {
        var nQueens = new NQueens();
        nQueens.addQueen(8);
    }

    public void addQueen(int n) {
        noSolutions = 0;
        solveQueens(new int[n][n], 0);
        System.out.println("Solutions for n=" + n + ": " + noSolutions);
    }


    private void solveQueens(int[][] state, int row) {
        for (int column = 0; column < state.length; column++) {
            if (isGoodPlace(state, row, column)) {
                reservePlace(state, row, column);
                if (row == state.length - 1) {
                    System.out.println(getStateString(state));
                    noSolutions++;
                } else {
                    solveQueens(state, row + 1);
                }
                cancelPlace(state, row, column);
            }
        }
    }

    private boolean isGoodPlace(int[][] state, int rowNumber, int columnNumber) {
        return isGoodColumn(state, columnNumber) &&
                isGoodTopDownColumn(state, rowNumber, columnNumber) &&
                isGoodBottomUpColumn(state, rowNumber, columnNumber);
    }

    private boolean isGoodColumn(int[][] state, int columnNumber) {
        boolean badColumn = false;
        for (var row : state) {
            if (row[columnNumber] == 1) {
                badColumn = true;
                break;
            }
        }
        return !badColumn;
    }

    private boolean isGoodTopDownColumn(int[][] state, int rowNumber, int columnNumber) {
        boolean badDiagonal = false;
        for (int i = rowNumber - 1, j = columnNumber - 1; i >= 0 && j >= 0; i--, j--) {
            if (state[i][j] == 1) {
                badDiagonal = true;
                break;
            }
        }
        return !badDiagonal;
    }

    private boolean isGoodBottomUpColumn(int[][] state, int rowNumber, int columnNumber) {
        boolean badDiagonal = false;
        for (int i = rowNumber - 1, j = columnNumber + 1; i >= 0 && j < state.length; i--, j++) {
            if (state[i][j] == 1) {
                badDiagonal = true;
                break;
            }
        }
        return !badDiagonal;
    }

    private void reservePlace(int[][] state, int row, int column) {
        state[row][column] = 1;
    }

    private void cancelPlace(int[][] state, int row, int column) {
        state[row][column] = 0;
    }

    private String getStateString(int[][] state) {
        var builder = new StringBuilder();
        for (var row : state) {
            for (var place : row) {
                builder.append(place == 0 ? "---" : " Q ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }


//    addQueen(rad)för kolumn 1 till 8om kolumn möjligboka platsenom rad==8skriv ut lösningannars addQueen(rad+1)avboka platsen
}

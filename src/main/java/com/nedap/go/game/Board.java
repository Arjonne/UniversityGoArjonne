package com.nedap.go.game;

/**
 * Represents the board of the GO game.
 */
public class Board {
    public static final int DIMENSION = 9; // represents the number of rows and columns of the board.

    // the board is represented as a 2D array:
    private Stone[][] board;
    private Board copyBoard; // can be removed in the end! only used for checking if copy goes well by printing this.

    /**
     * Creates an empty board.
     */
    public Board() {
        board = new Stone[DIMENSION][DIMENSION];
        reset();
    }

    /**
     * Creates a deep copy of this field.
     */
    public Board deepCopy() {
        copyBoard = new Board();
        for (int row = 0; row < DIMENSION; row++) {
            for (int column = 0; column < DIMENSION; column++) {
                copyBoard.board[row][column] = board[row][column];
            }
        }
        return copyBoard;
    }

    public void printBoard() { // can be deleted in the end; only check to see how/if it works as expected.
        // print the column numbers on top
        System.out.print("   ");
        for (int column = 0; column < DIMENSION; column++) {
            System.out.printf("%-3s", column);
        }
        System.out.println();

        // print the row numbers on the left side.
        for (int row = 0; row < DIMENSION; row++) {
            System.out.printf("%-3s", row);

            // print the state of the field.
            for (int column = 0; column < DIMENSION; column++) {
                if ((board[row][column]) == Stone.EMPTY) {
                    System.out.printf("%-2s", '.');
                } else if ((board[row][column]) == Stone.BLACK) {
                    System.out.printf("%-2s", 'B');
                } else {
                    System.out.printf("%-2s", 'W');
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void printCopyBoard() { // can be deleted in the end; only check to see how/if it works as expected.
        // print the column numbers on top
        System.out.print("   ");
        for (int column = 0; column < DIMENSION; column++) {
            System.out.printf("%-3s", column);
        }
        System.out.println();

        // print the row numbers on the left side.
        for (int row = 0; row < DIMENSION; row++) {
            System.out.printf("%-3s", row);

            // print the state of the field.
            for (int column = 0; column < DIMENSION; column++) {
                if ((copyBoard.board[row][column]) == Stone.EMPTY) {
                    System.out.printf("%-2s", '.');
                } else if ((copyBoard.board[row][column]) == Stone.BLACK) {
                    System.out.printf("%-2s", 'B');
                } else {
                    System.out.printf("%-2s", 'W');
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    /**
     * Checks whether the field a player wants to set a stone does exist.
     * @param row is the row of interest;
     * @param column is the column of interest;
     * @return true if the combination of row and column does exist; otherwise, isField() returns false.
     */
    public boolean isPosition(int row, int column) {
        if (row >= 0 && row < DIMENSION && column >= 0 && column < DIMENSION) {
            return true;
        }
        System.out.println("This is not a valid position. Both the row number and column number should be in the range 0 - " + (DIMENSION-1) + ".");
        return false;
    }

    /**
     * Gets the mark on a specific position
     * @param row is the row of interest;
     * @param column is the column of interest;
     * @return the Stone that is placed on that position; can be Empty as well if no stone is placed.
     */
    public Stone getPosition(int row, int column) {
        return board[row][column];
    }

    /**
     * Checks whether the position is empty
     * @param row is the row of interest;
     * @param column is the column of interest;
     * @return true if the position is empty; if result != Stone.EMPTY, return false.
     */
    public boolean isEmptyPosition(int row, int column) {
        if (getPosition(row, column) == Stone.EMPTY) {
            return true;
        }
        System.out.println("This position is already in use: the stone on this position is " + getPosition(row, column));
        return false;
    }

    /**
     * Sets the stone of interest on a specific position (after checking if this position does exist and if this
     * position is still empty).
     * @param row is the row of interest;
     * @param column is the column of interest;
     * @param stone is the stone of interest.
     */
    public void setPosition(int row, int column, Stone stone) {
        if (isPosition(row, column) && isEmptyPosition(row, column)) {
            board[row][column] = stone;
        }
    }

    /**
     * Empties all positions of this board (i.e., let all fields refer to the value Stone.EMPTY).
     */
    public void reset() {
        for (int row = 0; row < DIMENSION; row++) {
            for (int column = 0; column < DIMENSION; column++) {
                board[row][column] = Stone.EMPTY;
            }
        }
    }

//    public static void main(String[] args) {
//        Board board = new Board();
//        board.printBoard();
//        board.deepCopy();
//        board.printCopyBoard();
//        board.setPosition(3,4,Stone.BLACK);
//        board.setPosition(3,1,Stone.WHITE);
//        board.getPosition(3,4);
//        board.setPosition(11,3,Stone.WHITE);
//        board.setPosition(3,4,Stone.WHITE);
//        board.printBoard();
//        board.deepCopy();
//        board.printCopyBoard();
//    }
}

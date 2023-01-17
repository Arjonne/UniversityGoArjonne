package com.nedap.go.game;

/**
 * Represents the board of the GO game.
 */
public class Board {
    public static final int SIZE = 9; // represents the number of rows and columns of the board.
    private final Stone[][] board; // the board is represented as a 2D array.

    /**
     * Creates an empty board.
     */
    public Board() {
        board = new Stone[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                board[row][column] = Stone.EMPTY;
            }
        }
    }

    /**
     * Creates a copy of this field.
     */
    public Board copyBoard() {
        Board copyBoard = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                copyBoard.board[row][column] = board[row][column];
            }
        }
        return copyBoard;
    }

    /**
     * Checks whether the position a player wants to place a stone is valid (= does exist).
     *
     * @param row    is the row of interest;
     * @param column is the column of interest;
     * @return true if the combination of row and column does exist; otherwise, isValidPosition() returns false.
     */
    public boolean isValidPosition(int row, int column) {
        if (row >= 0 && row < SIZE && column >= 0 && column < SIZE) {
            return true;
        }
        System.out.println("The combination of row " + row + " and column " + column + " does not represent a valid position. Both the row number and column number should be in the range 0 - " + (SIZE - 1) + ".");
        return false;
    }

    /**
     * Gets the stone on a specific position.
     *
     * @param row    is the row of interest;
     * @param column is the column of interest;
     * @return the Stone that is placed on that position; can be EMPTY as well if no stone is placed.
     */
    public Stone getStone(int row, int column) {
        if (!isValidPosition(row, column)) {
            return Stone.EMPTY;
            //TODO check return? Of hoort deze check hier sowieso niet, maar in Game class?
        }
        return board[row][column];
    }

    /**
     * Checks whether the position is empty
     *
     * @param row    is the row of interest;
     * @param column is the column of interest;
     * @return true if the position is empty; if position is not valid OR result != Stone.EMPTY, return false.
     */
    public boolean isEmptyPosition(int row, int column) {
        if (!isValidPosition(row, column)) {
            return false; //TODO check return? Of hoort dit hier uberhaupt niet maar in Game class?
        }
        if (getStone(row, column) != Stone.EMPTY) {
            System.out.println("This position is already in use: the stone on this position is " + getStone(row, column) + ".");
            return false;
        }
        return true;
    }

    /**
     * Check whether all positions on the board are filled with black and white stones.
     *
     * @return true if all positions are filled, false if one position is still empty.
     */
    public boolean isFull() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (board[row][column] == Stone.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Places the stone of interest on a specific position (after checking if this position is valid and if this
     * position is empty).
     *
     * @param row    is the row of interest;
     * @param column is the column of interest;
     * @param stone  is the stone of interest.
     */
    public void placeStone(int row, int column, Stone stone) {
        if (isValidPosition(row, column) && isEmptyPosition(row, column)) {
            board[row][column] = stone;
        }
    }
//    public void printBoard() { // can be deleted in the end; only check to see how/if it works as expected.
//        // print the column numbers on top
//        System.out.print("   ");
//        for (int column = 0; column < SIZE; column++) {
//            System.out.printf("%-3s", column);
//        }
//        System.out.println();
//
//        // print the row numbers on the left side.
//        for (int row = 0; row < SIZE; row++) {
//            System.out.printf("%-3s", row);
//
//            // print the state of the field.
//            for (int column = 0; column < SIZE; column++) {
//                if ((board[row][column]) == Stone.EMPTY) {
//                    System.out.printf("%-2s", '.');
//                } else if ((board[row][column]) == Stone.BLACK) {
//                    System.out.printf("%-2s", 'B');
//                } else {
//                    System.out.printf("%-2s", 'W');
//                }
//                System.out.print(" ");
//            }
//            System.out.println();
//        }
//    }
//    public static void main(String[] args) {
//        Board board = new Board();
//        board.printBoard();
//        board.copyBoard();
//        board.placeStone(3, 4, Stone.BLACK);
//        board.placeStone(3, 1, Stone.WHITE);
//        board.getStone(3, 4);
//        board.placeStone(11, 3, Stone.WHITE);
//        board.placeStone(3, 4, Stone.WHITE);
//        board.printBoard();
//        board.copyBoard();
//        board.isEmptyPosition(11, 2);
//        board.placeStone(14, 2, Stone.BLACK);
//        board.getStone(11, 2);
//        board.placeStone(0,0,Stone.BLACK);
//        board.isBoardFull();
//    }
}

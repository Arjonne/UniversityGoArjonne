package com.nedap.go.game;

/**
 * Represents the board of the GO game.
 */
public class Board {
    public static final int SIZE = 9; // represents the number of rows and columns of the board
    private final Stone[][] board; // the board is represented as a 2D array, filled with stones (either BLACK, WHITE or EMPTY)

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
     * Creates a copy of the current state of the board.
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
     * Checks whether the position a player wants to place a stone is valid (is within the boundaries of the board).
     *
     * @param row    is the row of interest
     * @param column is the column of interest
     * @return true if the combination of row and column does exist on the board; if not, return false
     */
    public boolean isValidPosition(int row, int column) {
        if (row >= 0 && row < SIZE && column >= 0 && column < SIZE) {
            return true;
        }
        System.out.println("The combination of row " + row + " and column " + column + " does not represent a valid position. Both the row number and column number should be in the range 0 - " + (SIZE - 1) + ".");
        return false;
    }

    /**
     * Gets the stone that is placed on a specific position.
     *
     * @param row    is the row of interest
     * @param column is the column of interest
     * @return the Stone that is placed on that position; can be EMPTY as well if no stone is placed
     */
    public Stone getStone(int row, int column) {
        return board[row][column];
    }

    /**
     * Checks whether the position is empty.
     *
     * @param row    is the row of interest
     * @param column is the column of interest
     * @return true if the position is empty; if not, return false
     */
    public boolean isEmptyPosition(int row, int column) {
        if (getStone(row, column) != Stone.EMPTY) {
            //TODO message misschien niet hier vermelden, want dan iedere keer bij removal ook melding.
            System.out.println("This position is already in use: the stone on this position is " + getStone(row, column) + ".");
            return false;
        }
        return true;
    }

    /**
     * Checks whether all positions on the board are filled with black and/or white stones.
     *
     * @return true if all positions are filled; if (at least) one position is still EMPTY, return false
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
     * @param row    is the row of interest
     * @param column is the column of interest
     * @param stone  is the stone to place
     */
    public void placeStone(int row, int column, Stone stone) {
        // First of all, the position should be valid (within the boundaries of the board). Furthermore, to be able to
        // place a stone, this stone must be either BLACK or WHITE, and the position must be EMPTY.
        if (isValidPosition(row, column) && stone != Stone.EMPTY && isEmptyPosition(row, column)) {
            board[row][column] = stone;
        }
    }

    /**
     * Removes a stone from a specific position (after checking if this position is valid and a stone is available on
     * this position).
     *
     * @param row    is the row of interest
     * @param column is the column of interest
     */
    public void removeStone(int row, int column) {
        // First of all, the position should be valid (within the boundaries of the board). Furthermore, to be able to
        // remove a stone, the position must NOT be EMPTY.
        if (isValidPosition(row, column) && !isEmptyPosition(row, column)) {
            board[row][column] = Stone.EMPTY;
        }
    }

    /**
     * Creates a representation of the board of type String.
     */
    public String toString() {
        String stringRepresentationOfBoard = "";
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if ((board[row][column]) == Stone.EMPTY) {
                    stringRepresentationOfBoard += ".";
                } else if ((board[row][column]) == Stone.BLACK) {
                    stringRepresentationOfBoard += "B";
                } else {
                    stringRepresentationOfBoard += "W";
                }
            }
        }
        return stringRepresentationOfBoard;
    }

    public void printBoard() { // can be deleted in the end; only check to see how/if it works as expected.
        // print the column numbers on top
        System.out.print("   ");
        for (int column = 0; column < SIZE; column++) {
            System.out.printf("%-3s", column);
        }
        System.out.println();

        // print the row numbers on the left side.
        for (int row = 0; row < SIZE; row++) {
            System.out.printf("%-3s", row);

            // print the state of the field.
            for (int column = 0; column < SIZE; column++) {
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
}

package com.nedap.go.game;

/**
 * Represents the position on the board where a stone can be placed.
 */
public class Position {
    private int row;
    private int column;

    /**
     * Creates a position, based on a row and column input.
     *
     * @param row    is the row of interest;
     * @param column is the column of interest.
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row of the position.
     *
     * @return the row of the position.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column of the position.
     *
     * @return the column of the position.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Overrides the equals methods to be able to compare the actual positions instead of hashCodes of positions.
     *
     * @param position is the position that is compared with this methods
     * @return true if both the row and column of the two positions to compare are equal; false if not
     */
    @Override
    public boolean equals(Object position) {
        if (!(position instanceof Position)) {
            return false;
        }
        // Cast Object position to Position position to be able to compare to 'this' (which is a Position).
        return (((Position) position).getColumn() == this.getColumn()) && (((Position) position).getRow()) == this.getRow();
    }

    /**
     * Overrides the hashCode methods to be able to compare the actual positions instead of hashCodes of positions.
     *
     * @return a unique code for each position
     */
    @Override
    public int hashCode() {
        return (row * 100 + column);
    }
}

package com.nedap.go.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * methods and test that are not used yet and might not be necessary, but created and working already. Might need an
 * update, might not be useful at all, but check on later point!
 */
public class CHECKEN {

    // uit GAME class:
    private Position lastMoveBlack;
    private Position lastMoveWhite;
    private Set<Position> emptyPositionsSet;
    Map<Position, Stone> movesMap = new HashMap<>(); // can be made in constructor?


    //add to constructor:
    // create an empty list for storing the empty positions in a list
    emptyPositionsSet = new HashSet<>();

    /**
     * Create a set that stores all positions on which no stone is placed. In this set, all possible positions
     * (combination of int row and int column) are shown and stored as in this set (as each position is unique).
     * As all positions are empty at the start of the game, all positions are added to this set by creating it.
     *
     * @return the complete set of empty positions
     */
    public Set<Position> createEmptyPositionSet() {
        for (int row = 0; row < board.SIZE; row++) {
            for (int column = 0; column < board.SIZE; column++) {
                Position position = new Position(row, column);
                emptyPositionsSet.add(position);
            }
        }
        return emptyPositionsSet;
    }

    /**
     * Add the position of an empty position after removing a stone from the board
     *
     * @param row    is the row a stone is removed
     * @param column is the column a stone is removed
     * @return an update of the emptyPositionSet
     */
    public Set<Position> addEmptyPositionsToSet(int row, int column) {
        // First, find the correct position corresponding to the row and column of input. When this position is found,
        // the corresponding stone can be updated (remove this position from the set after placing a stone, add this
        // position after removing a stone from the board).
        emptyPositionsSet.add(new Position(row, column));
        return emptyPositionsSet;
    }

    /**
     * Remove an empty position after adding a stone on the board
     *
     * @param row    is the row a stone is placed
     * @param column is the column a stone is placed
     * @return an update of the emptyPositionSet
     */
    public Set<Position> removeEmptyPositionsFromSet(int row, int column) {
        for (Position positions : emptyPositionsSet) {
            if (positions.getRow() == row && positions.getColumn() == column) {
                emptyPositionsSet.remove(positions);
            }
        }
        return emptyPositionsSet;
    }

//    DOES WORK, BUT CHECK WHETHER NECESSARY?!
//
//    /**
//     * Create a map that stores all moves. In this map, all possible positions (combination of int row and int column)
//     * are shown and stored as keys (as each position is unique) and the values of these positions are the stones that
//     * are placed on this position (which can be EMPTY too). By the start of the game, all positions are EMPTY.
//     *
//     * @return a map with all positions of the board representing Stone.EMPTY.
//     */
//    public Map<Position, Stone> createMovesMap() {
//        for (int row = 0; row < board.SIZE; row++) {
//            for (int column = 0; column < board.SIZE; column++) {
//                Position position = new Position(row, column);
//                movesMap.put(position, Stone.EMPTY);
//            }
//        }
//        return movesMap;
//    }
//
//    /**
//     * Update the map after placing or removing a stone on/from the board.
//     *
//     * @param row    is the row a stone is placed or removed
//     * @param column is the column a stone is placed or removed
//     * @param stone  is the stone that is placed (for placing a stone, this is either BLACK or WHITE; for removal,
//     *               this is EMPTY)
//     * @return an updated map that represents all positions with corresponding stones that are currently placed on the board
//     */
//    public Map<Position, Stone> updateMovesMap(int row, int column, Stone stone) {
//        // First, find the correct position corresponding to the row and column of input. When this position is found,
//        // the corresponding stone can be updated (from EMPTY to BLACK or WHITE after placing a stone, from BLACK or
//        // WHITE to EMPTY after removing a stone).
//        for (Position positions : movesMap.keySet()) {
//            if (positions.getRow() == row && positions.getColumn() == column) {
//                movesMap.put(positions, stone);
//            }
//        }
//        // movesMap.put(new Position(row, column), stone);
//        return movesMap;
//    }


    /**
     * Get the last move that player black did to prevent KO.
     *
     * @return the position of the last move of player black
     */
    public Position getLastMoveBlack() { // check whether is necessary
        return lastMoveBlack;
    }

    /**
     * Get the last move that player white did to prevent KO.
     *
     * @return the position of the last move of player white
     */
    public Position getLastMoveWhite() { // check whether is necessary
        return lastMoveWhite;
    }

//    /**
//     * @param row    is the row a player has placed a stone
//     * @param column is the column a player has placed a stone
//     */
//    public void checkNeighbours(int row, int column, Stone stone) {
//        neighbourNorth = board.getStone(row, (column - 1));
//        neighbourEast = board.getStone((row + 1), column);
//        neighbourSouth = board.getStone(row, (column + 1));
//        neighbourWest = board.getStone((row - 1), column);
//    }

    //    /**
//     * Check whether a single stone is captured after making a move
//     *
//     * @return true if this stone is captured, false if it is not captured
//     */
//    public boolean isSingleStoneCaptured(int row, int column) {
//        checkNeighbours(row, column);
//        if (neighbourNorth == getStoneOpponent(currentPlayer) &&
//                neighbourEast == getStoneOpponent(currentPlayer) &&
//                neighbourSouth == getStoneOpponent(currentPlayer) &&
//                neighbourWest == getStoneOpponent(currentPlayer)) {
//            System.out.println("The stone of " + currentPlayer.getUsername() + " on position: row " + row + " and column " + column + " is captured and will be removed!");
//            removeStone(row, column);
//            return true;
//        }
//        return false;
//    }



}

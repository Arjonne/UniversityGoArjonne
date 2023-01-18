package com.nedap.go.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the GO game, including rules.
 */

public class Game {
    private Player playerBlack;
    private Player playerWhite;
    private Board board;
    private Player currentPlayer;
    private int passCount;
    private Position lastMoveBlack;
    private Position lastMoveWhite;
    Map<Position, Stone> movesMap = new HashMap<>();

    /**
     * Creates a new game with two players and a board.
     *
     * @param playerBlack player with black stones
     * @param playerWhite player with white stones
     * @param board       the game board
     */
    public Game(Player playerBlack, Player playerWhite, Board board) {
        this.playerBlack = playerBlack;
        this.playerWhite = playerWhite;
        this.board = board;
        // as Black always starts the game, this player is assigned to currentPlayer in the constructor
        currentPlayer = playerBlack;
        // each game starts with a pass count of 0 (after two consecutive passes, the game is over).
        passCount = 0;
    }

    /**
     * Switch from player (after a move or pass is done).
     */
    public void switchTurn() {
        if (currentPlayer == playerBlack) {
            currentPlayer = playerWhite;
        } else {
            currentPlayer = playerBlack;
        }
    }

    /**
     * Get the stone of the current player.
     *
     * @param currentPlayer is the player that currently can make a move or pass
     * @return the stone this player uses
     */
    public Stone getStone(Player currentPlayer) {
        if (currentPlayer == playerBlack) {
            return Stone.BLACK;
        } else {
            return Stone.WHITE;
        }
    }

    /**
     * Get the stone of the opponent of the current player.
     *
     * @param currentPlayer is the player that currently can make a move or pass
     * @return the stone the opponent player uses
     */
    public Stone getStoneOpponent(Player currentPlayer) {
        if (currentPlayer == playerBlack) {
            return Stone.WHITE;
        } else {
            return Stone.BLACK;
        }
    }

    /**
     * Get the player whose turn it is.
     *
     * @param currentPlayer is the player that currently can make a move or pass
     * @return the player that can make a move or pass (either playerBlack or playerWhite)
     */
    public Player getTurn(Player currentPlayer) {
        return currentPlayer;
    }

    /**
     * Create a map that stores all moves. In this map, all possible positions (combination of int row and int column)
     * are shown and stored as keys (as each position is unique) and the values of these positions are the stones that
     * are placed on this position (which can be EMPTY too). By the start of the game, all positions are EMPTY.
     *
     * @return a map with all positions of the board representing Stone.EMPTY.
     */
    public Map<Position, Stone> createMovesMap() {
        for (int row = 0; row < board.SIZE; row++) {
            for (int column = 0; column < board.SIZE; column++) {
                Position position = new Position(row, column);
                movesMap.put(position, Stone.EMPTY);
            }
        }
        return movesMap;
    }

    /**
     * Update the map after placing or removing a stone on/from the board.
     *
     * @param row    is the row a stone is placed or removed
     * @param column is the column a stone is placed or removed
     * @param stone  is the stone that is placed (for placing a stone, this is either BLACK or WHITE; for removal,
     *               this is EMPTY)
     * @return an updated map that represents all positions with corresponding stones that are currently placed on the board
     */
    public Map<Position, Stone> updateMovesMap(int row, int column, Stone stone) {
        // First, find the correct position corresponding to the row and column of input. When this position is found,
        // the corresponding stone can be updated (from EMPTY to BLACK or WHITE after placing a stone, from BLACK or
        // WHITE to EMPTY after removing a stone).
        for (Position positions : movesMap.keySet()) {
            if (positions.getRow() == row && positions.getColumn() == column) {
                movesMap.put(positions, stone);
            }
        }
        return movesMap;
    }

    /**
     * Check whether the move a player wants to make, is a valid move.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return true if the move is valid or false if it is not valid
     */
    public boolean isValidMove(int row, int column) { // check if position is EMPTY && lastMove != newMove && of positie op het board is.
//        //TODO also check via MAP instead of BOARD!
//        for (Position positions : movesMap.keySet()) {
//            if (positions.getRow() == row && positions.getColumn() == column) {
//                if (movesMap.get(positions).equals(Stone.EMPTY)) {
//                    return false;
//                }
//            }
        // check if position is a valid position on the board
        if (!board.isValidPosition(row, column)) {
            return false;
        }
        // check if no stone has been placed on this position yet
        if (!board.isEmptyPosition(row, column)) {
            return false;
        }
        // check if this move is not the same as the last move of this player (KO rule) - can only be possible if this
        // stone is captured by the opponent in between.
        if (currentPlayer == playerBlack && lastMoveBlack != null) {
            if (lastMoveBlack.getRow() == row && lastMoveBlack.getColumn() == column) {
                return false;
            }
        } else if (currentPlayer == playerWhite && lastMoveWhite != null) {
            if (lastMoveWhite.getRow() == row && lastMoveWhite.getColumn() == column) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether a single stone is captured after making a move
     *
     * @return true if this stone is captured, false if it is not captured
     */
    public boolean isSingleStoneCaptured(int row, int column) {
        // TODO: nu via BOARD, mogelijk ook via MAP checken --> wat is beter?!
        //  Daarnaast: uitzonderingen voor als steen in op een rand / in de hoek geplaatst is!
        if (board.getStone((row - 1), column) == getStoneOpponent(currentPlayer) &&
                board.getStone((row + 1), column) == getStoneOpponent(currentPlayer) &&
                board.getStone(row, (column - 1)) == getStoneOpponent(currentPlayer) &&
                board.getStone(row, (column + 1)) == getStoneOpponent(currentPlayer)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether or multiple stones are captured after making a move
     *
     * @return true if multiple stones are captured, false if they are not captured
     */
    public boolean areMultipleStonesCaptured() {
        //TODO
        return false;
    }

    /**
     * Execute the move when this move is found to be valid.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     */
    public void doMove(int row, int column) {
        if (isValidMove(row, column)) {
            board.placeStone(row, column, getStone(currentPlayer));
            // reset passCount if move is made
            passCount = 0;
            // update movesMap with the position and stone that are used in this move
            updateMovesMap(row, column, getStone(currentPlayer));
            // update lastMove of current player to this move, so that the check can be done on this move during the next turn
            if (getStone(currentPlayer) == Stone.BLACK) {
                lastMoveBlack = new Position(row, column);
            } else {
                lastMoveWhite = new Position(row, column);
            }
            // after making a move, it is the turn of the opponent.
            switchTurn();
        }
    }

    /**
     * Remove stones from the board after capturing these stones.
     *
     * @param row    is the row the stone to be removed is positioned
     * @param column is the column the stone to be removed is positioned
     */
    public void removeStone(int row, int column) {
        //   if (isCaptured()) {
        board.removeStone(row, column);
        updateMovesMap(row, column, Stone.EMPTY);
        //      }
    }

    /**
     * Passing without placing a stone.
     */
    public void pass() {
        // update passCount: when two consecutive passes are made, the game is over
        passCount++;
        // the turn goes to the opponent without placing a stone
        switchTurn();
    }

    /**
     * Check whether the game is over.
     *
     * @return true if two consecutive passes are done, otherwise false
     */
    public boolean isGameOver() {
        return passCount == 2;
    }

    public Player getWinner() {
        return null;
    }

    public static void main(String[] args) {
        Game game = new Game(new Player("black", Stone.BLACK), new Player("white", Stone.WHITE), new Board());
        game.createMovesMap();
        game.doMove(0, 0);
        System.out.println(game.movesMap);
        game.doMove(1, 4);
        game.removeStone(0, 0);
        game.doMove(0, 0);
        game.pass();
        game.pass();
        game.isGameOver();
    }


// OK A move consists of placing one stone of a player their own color on an empty intersection on the board.
// OK A player may pass their turn at any time.
// A stone or solidly connected group of stones of one color is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// Self-capture/suicide is allowed (this is different from the commonly used rules).
// When a suicide move results in capturing a group, the group is removed from the board first and the suiciding stone stays alive.
// No stone may be played so as to recreate any previous board position (ko rule https://en.wikipedia.org/wiki/Rules_of_go#Repetition).
// OK Two consecutive passes will end the game.
// A player's area consists of all the intersections the player has either occupied or surrounded.
// The player with the biggest area wins. This way of scoring is called Area Scoring
// https://en.wikipedia.org/wiki/Rules_of_go#Area_scoring. In case of an equal score, there is a draw.

    // The oldest counting method is as follows: At the end of the game, all white stones are removed from the board, and the players use black stones to fill the entirety of the black territory. Score is determined by counting the black stones. Since the board contains 361 intersections, black must have 181 or more stones to win. This method is still widely used in Mainland China.
    //Around 1975, Taiwanese player and industrialist Ing Chang-ki invented a method of counting now known as Ing counting. Each player begins the game with exactly 180 stones (Ing also invented special stone containers that count each player's stones). At the end, all stones are placed on the board. One vacant intersection will remain, appearing in the winner's area; the number of stones of one color in the other color's area will indicate the margin of victory.
}
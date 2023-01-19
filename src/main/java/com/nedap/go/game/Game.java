package com.nedap.go.game;

import javafx.geometry.Pos;

import java.util.*;

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
        // each game starts with a pass count of 0 (after two consecutive passes, the game is over)
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
     * Get the board
     *
     * @return the board of the game
     */
    public Board getBoard() {
        return board;
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
     * Get the player who is currently playing.
     *
     * @return the player that can make a move or pass (either playerBlack or playerWhite)
     */
    public Player getCurrentPlayer() {
        if (currentPlayer == playerBlack) {
            return playerBlack;
        } else {
            return playerWhite;
        }
    }

    /**
     * Check whether the move a player wants to make, is a valid move.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return true if the move is valid or false if it is not valid
     */
    public boolean isValidMove(int row, int column) {
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
//            updateMovesMap(row, column, getStone(currentPlayer));
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
     * Get the positions of the neighbours of the placed stone.
     *
     * @param row    is the row a player has placed a stone
     * @param column is the column a player has placed a stone
     * @return a set of positions of the neighbours of the placed stone
     */
    public Set<Position> getNeighbourPositions(int row, int column) {
        Set<Position> neighbourPositions = new HashSet<>();
        if (row != 0) {
            neighbourPositions.add(new Position((row - 1), column));
        }
        if (row != (board.SIZE - 1)) {
            neighbourPositions.add(new Position((row + 1), column));
        }
        if (column != 0) {
            neighbourPositions.add(new Position(row, (column - 1)));
        }
        if (column != (board.SIZE - 1)) {
            neighbourPositions.add(new Position(row, (column + 1)));
        }
        return neighbourPositions;
    }

    /**
     * Remove stones from the board after capturing these stones.
     *
     * @param row    is the row the stone to be removed is positioned
     * @param column is the column the stone to be removed is positioned
     */
    public void removeStone(int row, int column) {
        board.removeStone(row, column);
//            updateMovesMap(row, column, Stone.EMPTY);
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
     * Get the passCounter (number of consecutive passes)
     *
     * @return the number of consecutive passes
     */
    public int getPassCount() {
        return passCount;
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
//        game.createMovesMap();
//        game.doMove(0, 0);
//        System.out.println(game.movesMap);
//        game.doMove(1, 4);
//        game.removeStone(0, 0);
//        game.doMove(0, 0);
//        game.pass();
//        game.pass();
//        game.isGameOver();
        game.doMove(3, 3); // black
        game.doMove(3, 4); // white
        game.doMove(0, 0); // B
        game.doMove(2, 3); // W
        game.doMove(0, 1); // B
        game.doMove(3, 2); // W
        game.doMove(1, 0); // B
        game.doMove(4, 3); // W
        game.getBoard().printBoard();
        //
    }


// OK A move consists of placing one stone of a player their own color on an empty intersection on the board.
// OK A player may pass their turn at any time.
// OK A stone is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// A solidly connected group of stones of one color is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// OK Self-capture/suicide is allowed (this is different from the commonly used rules). = on each empty field a stone can be placed.
// When a suicide move results in capturing a group, the group is removed from the board first and the suiciding stone stays alive.
// OK No stone may be played so as to recreate any previous board position (ko rule https://en.wikipedia.org/wiki/Rules_of_go#Repetition).
// OK Two consecutive passes will end the game.
// A player's area consists of all the intersections the player has either occupied or surrounded.
// The player with the biggest area wins. This way of scoring is called Area Scoring
// https://en.wikipedia.org/wiki/Rules_of_go#Area_scoring. In case of an equal score, there is a draw.

    // The oldest counting method is as follows: At the end of the game, all white stones are removed from the board, and the players use black stones to fill the entirety of the black territory. Score is determined by counting the black stones. Since the board contains 361 intersections, black must have 181 or more stones to win. This method is still widely used in Mainland China.
    //Around 1975, Taiwanese player and industrialist Ing Chang-ki invented a method of counting now known as Ing counting. Each player begins the game with exactly 180 stones (Ing also invented special stone containers that count each player's stones). At the end, all stones are placed on the board. One vacant intersection will remain, appearing in the winner's area; the number of stones of one color in the other color's area will indicate the margin of victory.
}
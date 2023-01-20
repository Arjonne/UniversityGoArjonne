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
    private List<String> listPreviousBoards;
    Set<Position> neighbourPositions;
    private Set<Position> neighbourOfNeighbourPositions;
    private Set<Position> neighbourPositionOtherPlayer;
    private int score;


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
        // create a list to store all previous boards (map representation)
        listPreviousBoards = new ArrayList<>();
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
     * Check whether the Ko rule is violated (which means, that the current state of the board has been
     * existing before and therefore this move is not a valid move).
     *
     * @param stringRepresentationOfBoard is the String representation of the current board
     * @return true if this state of the board has appeared before
     */
    public boolean isKoRuleViolated(String stringRepresentationOfBoard) {
        if (listPreviousBoards.isEmpty()) {
            listPreviousBoards.add(getBoard().toString());
            return false;
        }
        // loop through the list with Strings (in which all previous board states are saved)
        for (String stringRepresentationPreviousBoardStates : listPreviousBoards) {
            if (stringRepresentationPreviousBoardStates.equals(stringRepresentationOfBoard)) {
                System.out.println("The board has existed in the current state before, indicating that this is an" +
                        " invalid move (KO rule: a stone that will recreate a former board position may not be placed)!");
                return true;
            }
        }
        listPreviousBoards.add(stringRepresentationOfBoard);
        return false;
    }

    // as placeStone in Board class is used an uses exactly the same checks, it is probably not necessary to repeat here!

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
            // Check KO rule violation. If this rule is violated, the stone will be removed again and the player can try
            // a new move --> no switch turn.
            if (isKoRuleViolated(getBoard().toString())) {
                board.removeStone(row, column);
                System.out.println("This is not a valid move, try again."); // maybe to TUI, and recall doMove with other row/column input. OR pass!
            } else {
                if (hasCaptured(row, column)) {
                    for (Position neighbourPositionsOtherPlayer : neighbourPositionOtherPlayer) {
                        removeStone(neighbourPositionsOtherPlayer.getRow(),neighbourPositionsOtherPlayer.getColumn());
                    }
                }
                if (isCaptured(row, column)) {
                    //remove captured stones
                }
                // reset passCount if move is made
                passCount = 0;
                // after making a move, it is the turn of the opponent
                switchTurn();
            }
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
        neighbourPositions = new HashSet<>();
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
     * First, perform a check whether the placed stone actually has a neighbour with a stone of the opponent
     *
     * @param row    is the row a player has placed a stone
     * @param column is the column a player has placed a stone
     * @return true if the placed stone has a neighbour of the opponent, false if not
     */
    public boolean hasNeighbourStonesOfOpponent(int row, int column) {
        for (Position neighbourPositions : neighbourPositions) {
            if (board.getStone(neighbourPositions.getRow(), neighbourPositions.getColumn()) == getStoneOpponent(currentPlayer))
                return true;
        }
        return false;
    }

    /**
     * Get a list of the positions with the stones of the opponent that are located on the neighbour positions.
     *
     * @param row    is the row a player has placed a stone
     * @param column is the column a player has placed a stone
     * @return the list of positions with stones of the opponent that are located on the neighbour positions
     */
    public Set<Position> getNeighbourStones(int row, int column) {
        for (Position neighbourPositions : neighbourPositions) {
            if (board.getStone(neighbourPositions.getRow(), neighbourPositions.getColumn()) == getStoneOpponent(currentPlayer)) {
                neighbourPositionOtherPlayer = new HashSet<>(); // new set for each stone directly around the placed stone
                // (can be of different groups, compare later)
//                neighbourPositionOtherPlayer.add(neighbourPositions);
            }
        }
        return neighbourPositionOtherPlayer;
    }

    /**
     * After checking the neighbours of the recently placed stone, and concluding that one stone of the opponent is located
     * at a neighbour position, the neighbour positions of that stone need to be checked as well to see whether this stone
     * is located in a group. For that, the positions of the neighbour of that stone need to be found.
     *
     * @param neighbourPositionOtherPlayer is the set of neighbour positions on which a stone of the opponent is located.
     * @return a set with the positions of the neighbours of the checked positions
     */
    public Set<Position> getNeighbourPositionsOfNeighbours(Set<Position> neighbourPositionOtherPlayer) {
        neighbourOfNeighbourPositions = new HashSet<>();
        for (Position neighbourPosition : neighbourPositionOtherPlayer) {
            if (neighbourPosition.getRow() != 0) {
                neighbourOfNeighbourPositions.add(new Position((neighbourPosition.getRow() - 1), neighbourPosition.getColumn()));
            }
            if (neighbourPosition.getRow() != (board.SIZE - 1)) {
                neighbourOfNeighbourPositions.add(new Position((neighbourPosition.getRow() + 1), neighbourPosition.getColumn()));
            }
            if (neighbourPosition.getColumn() != 0) {
                neighbourOfNeighbourPositions.add(new Position(neighbourPosition.getRow(), (neighbourPosition.getColumn() - 1)));
            }
            if (neighbourPosition.getColumn() != (board.SIZE - 1)) {
                neighbourOfNeighbourPositions.add(new Position(neighbourPosition.getRow(), (neighbourPosition.getColumn() + 1)));
            }
        }
        return neighbourOfNeighbourPositions;
    }

    /**
     * After checking the neighbours of the recently placed stone, and concluding that one stone of the opponent is located
     * at a neighbour position, the neighbour positions of that stone need to be checked as well to see whether this stone
     * is located in a group. When the positions are known, the list of stones of the opponent can be extended when
     * a stone of the opponent is found.
     *
     * @param neighbourOfNeighbourPositions is the list with positions where the stone is located of which the neighbours are checked
     * @return the extended set of locations where a stone of the opponent is located
     */
    public Set<Position> getNeighbourStonesOfNeighbour(Set<Position> neighbourOfNeighbourPositions) {
        for (Position neighbourPositions : neighbourOfNeighbourPositions) {
            if (board.getStone(neighbourPositions.getRow(), neighbourPositions.getColumn()) == getStoneOpponent(currentPlayer)) {
                neighbourPositionOtherPlayer.add(neighbourPositions);
            }
        }
        return neighbourPositionOtherPlayer;
    }

    /**
     * Checks whether a neighbour (a stone of the opponent) has an empty field as neighbour, since he is NOT caputured
     * in that case.
     *
     * @param neighbourOfNeighbourPositions is the list with positions where the stone is located of which the neighbours are checked
     * @return true if the stone of the opponent has an empty position as neighbour, false if no empty position is located next to the neighbour
     */
    public boolean hasNeighbourEmptyNeighbour(Set<Position> neighbourOfNeighbourPositions) {
        for (Position neighbourPositions : neighbourOfNeighbourPositions) {
            if (board.getStone(neighbourPositions.getRow(), neighbourPositions.getColumn()) == Stone.EMPTY) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCaptured(int row, int column) {
        // zoek de posities van je buurstenen op
        getNeighbourPositions(row, column);
        // zoek de stenen op die op deze posities liggen
        getNeighbourStones(row, column);
        // als er direct naast deze geplaatste steen GEEN steen van de tegenstander ligt, dan heb je sowieso niets omsloten met deze zet
        if (!hasNeighbourStonesOfOpponent(row, column)) {
            return false;
        }
        // als er (minimaal een) steen van de tegenstander naast de geplaatste steen ligt, ga je 'naar deze steen toe en bekijk je de
        // posities van de buren daarvan'.
        getNeighbourPositionsOfNeighbours(neighbourPositionOtherPlayer);
        // check vervolgens of deze buur aan minimaal een kant een empty field heeft, want dan is 'ie sowieso niet ingesloten.
        if (hasNeighbourEmptyNeighbour(neighbourOfNeighbourPositions)) {
            return false;
        }
        // als de buren allemaal bezet zijn, haal vervolgens de stenen op van de tegenstander die geplaatst zijn op deze posities van de buren:
        getNeighbourStonesOfNeighbour(neighbourOfNeighbourPositions);
        //////// HIER GAAT RECURSIE IN, want hier ga je weer de posities en stenen checken van de buren met de kleur van de tegenstander etc.

        return true;
    }

    public boolean isCaptured(int row, int column) {
        return false;
    }

    /**
     * Remove stones from the board after capturing these stones.
     *
     * @param row    is the row the stone to be removed is positioned
     * @param column is the column the stone to be removed is positioned
     */
    public void removeStone(int row, int column) {
        board.removeStone(row, column);
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
     * @return true if two consecutive passes are done OR all positions on the board are filled, otherwise false
     */
    public boolean isGameOver() {
        if (passCount == 2 || getBoard().isFull()) {
            return true;
        }
        return false;
    }

    /**
     * Determine the final score of the player.
     *
     * @param player is player of interest
     * @return the final score
     */
    public int finalScore(Player player) {
        score = 0;
        return score;
    }

    /**
     * Get the winner of this game.
     *
     * @return the player with the most points; can be null if the game ended in a draw
     */
    public Player getWinner() {
        if (finalScore(playerBlack) > finalScore(playerWhite)) {
            return playerBlack;
        } else if (finalScore(playerBlack) < finalScore(playerWhite)) {
            return playerWhite;
        } else {
            System.out.println("This game ended in a draw!");
            return null;
        }
    }

    public static void main(String[] args) {
        Game game = new Game(new Player("black", Stone.BLACK), new Player("white", Stone.WHITE), new Board());
        game.doMove(0, 0);
//        System.out.println(game.movesMap);
        game.doMove(1, 4);
        game.removeStone(0, 0);
        game.doMove(0, 0);
        // check two passes result in gameOver
//        game.pass();
//        game.pass();
//        game.isGameOver();
        // check capturing one stone:
        game.doMove(3, 3); // black
        game.doMove(3, 4); // white
        game.doMove(0, 0); // B
        game.doMove(2, 3); // W
        game.doMove(0, 1); // B
        game.doMove(3, 2); // W
        game.doMove(1, 0); // B
        game.doMove(4, 3); // W
        game.getBoard().printBoard();
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


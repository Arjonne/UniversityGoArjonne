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
    private static int passCount; // probably static, but check if it works when game is finished!
    private List<String> listPreviousBoards;
    Set<Position> neighbourPositions;
    Set<Stone> neighbourStones;
    Set<Position> setOfPositions;
    Set<Position> setOfCheckedPositions;
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
        // create a list to store all previous states of the board (which are represented as a String) to be able to
        // check the ko rule
        listPreviousBoards = new ArrayList<>();
    }

    /**
     * Switches from player (after a move or pass is done).
     */
    public void switchTurn() {
        if (currentPlayer == playerBlack) {
            currentPlayer = playerWhite;
        } else {
            currentPlayer = playerBlack;
        }
    }

    /**
     * Gets the board.
     *
     * @return the board of the game
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the stone of the current player.
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
     * Gets the stone of the opponent of the current player.
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
     * Gets the player who is currently playing.
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

    // as placeStone in Board class is used an uses exactly the same checks, it is probably not necessary to repeat here!
    // OR maybe it is, as the game will probably on the server-side and the game on the client side, so double-checking
    // might be necessary

    /**
     * Checks whether the move a player wants to make, is a valid move.
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
     * Checks whether the Ko rule is violated (which means, that the current state of the board has been
     * existing before and therefore this move is not a valid move).
     *
     * @param stringRepresentationOfBoard is the String representation of the current board
     * @return true if this state of the board has appeared before
     */
    public boolean isKoRuleViolated(String stringRepresentationOfBoard) {
        // with the first move, this list is still empty so not necessary to loop through the list; the String can be
        // directly added without performing checks
        if (listPreviousBoards.isEmpty()) {
            listPreviousBoards.add(getBoard().toString());
            return false;
        }
        // loop through the list with Strings (in which all previous board states are saved) and compare the current
        // state of the board with all previous states
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

    // check captures (isCaputered and hasCaptured)

    /**
     * Gets a set with the positions of the neighbours of the placed stone.
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
     * Gets a set with the stones that are located on the neighbour positions.
     *
     * @return the list of positions with stones of the opponent that are located on the neighbour positions
     */
    public Set<Stone> getNeighbourStones() {
        neighbourStones = new HashSet<>();
        for (Position neighbourPosition : neighbourPositions) {
            Stone neighbourStone = board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn());
            neighbourStones.add(neighbourStone);
        }
        return neighbourStones;
    }

    /**
     * Checks whether the placed stone has a stone of the opponent as neighbour.
     *
     * @return true if the placed stone has a neighbour of the opponent, false if not
     */
    public boolean hasNeighbourStonesOfOpponent() {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == getStoneOpponent(currentPlayer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the placed stone has a stone of the own player as neighbour.
     *
     * @return true if the placed stone has a neighbour of the own player, false if not
     */
    public boolean hasNeighbourStonesOfOwn() {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == getStone(currentPlayer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a set for each direct neighbour of the placed stone that contains a stone of the opponent, to be able
     * to check whether these neighbours are part of a group of stones, and to check whether this neighbour stone (or
     * group of stones) is captured by placing this stone.
     */
    public void createSetOfPositions() {
        // if looking for stones of the opponent (for hasCaptured):
        for (Position neighbourPosition : neighbourPositions) {
            if (board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn()) == getStoneOpponent(currentPlayer)) {
                setOfPositions = new HashSet<>();
                setOfPositions.add(neighbourPosition);
            }
        }
    }

    /**
     * Creates a set of the placed stone, to be able to check whether this stone is part of a group of stones, and to
     * check whether this stone (or group of stones) is captured by placing this stone.
     *
     * @param row    is the row a player has placed a stone
     * @param column is the column a player has placed a stone
     */
    public void createSetOfPositions(int row, int column) {
        // if looking for stones of the own player (for isCaptured):
        setOfPositions = new HashSet<>();
        setOfPositions.add(new Position(row, column));
    }

    /**
     * Adds the position of the neighbour stone of the opponent to the set of stones that represents group of stones of
     * the opponent.
     */
    public void addOpponentToSetOfPositions() {
        for (Position neighbourPosition : neighbourPositions) {
            if (board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn()) == getStoneOpponent(currentPlayer)) {
                setOfPositions.add(neighbourPosition);
            }
        }
    }

    /**
     * Adds the position of the neighbour stone of the own player to the set of stones that represents group of stones of
     * the own player.
     */
    public void addOwnToSetOfPositions() {
        for (Position neighbourPosition : neighbourPositions) {
            if (board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn()) == getStone(currentPlayer)) {
                setOfPositions.add(neighbourPosition);
            }
        }
    }

    /**
     * Creates a new set to be able to keep track of the positions that are checked on the neighbour stones.
     */
    public void createSetOfCheckedPositions() {
        setOfCheckedPositions = new HashSet<>();
    }

    /**
     * Checks whether the checked stone has an empty neighbour position.
     *
     * @return true if the checked stone has an empty neighbour position, false if not
     */
    public boolean hasEmptyNeighbours() {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == Stone.EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the checked stone only has stones of own player as neighbour.
     *
     * @return true if the checked stone only has stones of own player as neighbour, false if not
     */
    public boolean hasOnlyNeighbourStonesOfOwn() {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone != getStone(currentPlayer)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the checked stone only has stones of opponent as neighbour.
     *
     * @return true if the checked stone only has stones of opponent as neighbour, false if not
     */
    public boolean hasOnlyNeighbourStonesOfOpponent() {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone != getStoneOpponent(currentPlayer)) {
                return false;
            }
        }
        return true;
    }

    public void loopHasCaptured() {

    }

    /**
     * Checks whether the current player has captured a stone or group of stones of the opponent with this move.
     * 1.  Find the position of the direct neighbours of the placed stone;
     * 2.  Find the stones located on these neighbour positions;
     * 3.  Check if at least 1 of the neighbour stones is of the opponent;
     * 4.  Create a new set for each of the neighbour stones of the opponent;
     * 5.  Create a new set for the checked stones (with locations of stones of the opponent);
     * 6.  Go into the set created by 4. and check the neighbour positions of that stone;
     * 7.  Find the stones that are located on these neighbour positions;
     * 8.  Check if at least one of those neighbour stones is EMPTY --> no capture!
     * 9.  Check if all neighbour stones are of own player --> has captured this single stone!
     * 10.  Check if any of the neighbours is of the opponent; when this is the case, add the stone(s) to the list
     * created in 4;
     * 11. Add the currently checked position to the set created by 5;
     * 12. Check whether the set with to checked stones has the same size as the set that represents all found stones
     * on neighbour positions of the opponent;
     * 13. --> if true, placed move has captured a group of stones! If not true, redo from 6 (loop through set);
     * 14. Check if the set with checked stones now contains all sets as created in 4. If not, check this / these
     * stone(s) too in the same way.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return true if this move leads to capturing a stone or group of stones of the opponent, false if not
     */
    public boolean hasCaptured(int row, int column) {
        // 1.  Find the position of the direct neighbours of the placed stone;
        getNeighbourPositions(row, column);
        // 2.  Find the stones located on these neighbour positions;
        getNeighbourStones();
        // 3.  Check if at least 1 of the neighbour stones is of the opponent;
        if (!hasNeighbourStonesOfOpponent()) {
            return false;
        }
        // 4.  Create a new set for each of the neighbour stones of the opponent;
        createSetOfPositions();
        // 5.  Create a new set for the checked stones (with locations of stones of the opponent);
        createSetOfCheckedPositions();

        loopHasCaptured();

        //TODO : HIER START RECURSIE! DUS VANAF HIER NIEUWE METHODE VAN AANMAKEN. ALS SIZE != SIZE, ZICHZELF WEER AANROEPEN!

        // 6.  Go into the set created by 4. and check the neighbour positions of that stone;
        for (Position position : setOfPositions) {
            getNeighbourPositions(position.getRow(), position.getColumn());
            // 7.  Find the stones that are located on these neighbour positions;
            getNeighbourStones();
            // 8.  Check if at least one of those neighbour stones is EMPTY --> no capture!
            if (hasEmptyNeighbours()) {
                return false;
            }
            // 9.  Check if all neighbour stones are of own player --> has captured this single stone!
            if (hasOnlyNeighbourStonesOfOwn()) {
                return true;
            }
            // 10. Check if any of the neighbours is of the opponent; when this is the case, add the stone(s) to the list
            //      created in 4;
            if (hasNeighbourStonesOfOpponent()) {
                addOpponentToSetOfPositions();
            }
            // 11. Add the currently checked position to the set created by 5;
            setOfCheckedPositions.add(position);
            // 12. Check whether the set with to checked stones has the same size as the set that represents all found stones
            //      on neighbour positions of the opponent;
            // 13. --> if true, placed move has captured a group of stones! If not true, redo from 6 (loop through set);
            if (setOfPositions.size() == setOfCheckedPositions.size()) {
                return true;
            }

            // EINDE RECUSION

            // 14. Check if the set with checked stones now contains all sets as created in 4. If not, check this / these
            //      stone(s) too in the same way.
            //TODO
        }
        return true;
    }

    /**
     * Checks whether the current player has self-captured (suicide) a stone or group of stones with this move.
     * 1.  Create a new set for each of the own stones and add current position to this set;
     * 2.  Create a new set for the checked stones (with locations of stones of own player);
     * 3.  Go into the set created by 1. and check the neighbour positions of that stone;
     * 4.  Find the stones located on these neighbour positions;
     * 5.  Check if at least one of those neighbour stones is EMPTY --> no capture!
     * 6.  Check if all neighbour stones are of opponent --> this single stone is captured!
     * 7.  Check if any of the neighbours is of the own player; when this is the case, add the stone(s) to the list
     * created in 1;
     * 8.  Add the currently checked position to the set created by 2;
     * 9.  Check whether the set with to checked stones has the same size as the set that represents all found stones
     * on neighbour positions of own player;
     * 10. --> if true, placed move is captured as a group of stones! If not true, redo from 3 (loop through set).
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return true if this move leads to a self-capture (suicide) with a single stone or group of stones, false if not
     */
    public boolean isCaptured(int row, int column) {
        // 1.  Create a new set for each of the own stones and add current position to this set;
        createSetOfPositions(row, column);
        // 2.  Create a new set for the checked stones (with locations of stones of own player);
        createSetOfCheckedPositions();

        //TODO : HIER START RECURSIE! DUS VANAF HIER NIEUWE METHODE VAN AANMAKEN. ALS SIZE != SIZE, ZICHZELF WEER AANROEPEN!

        // 3.  Go into the set created by 1. and check the neighbour positions of that stone;
        for (Position position : setOfPositions) {
            getNeighbourPositions(position.getRow(), position.getColumn());
            // 4.  Find the stones located on these neighbour positions;
            getNeighbourStones();
            // 5.  Check if at least one of those neighbour stones is EMPTY --> no capture!
            if (hasEmptyNeighbours()) {
                return false;
            }
            // 6.  Check if all neighbour stones are of opponent --> this single stone is captured!
            if (hasOnlyNeighbourStonesOfOpponent()) {
                return true;
            }
            // 7.  Check if any of the neighbours is of the own player; when this is the case, add the stone(s) to the list
            //      created in 1;
            if (hasNeighbourStonesOfOwn()) {
                addOwnToSetOfPositions();
            }
            // 8.  Add the currently checked position to the set created by 2;
            setOfCheckedPositions.add(position);
            // 9.  Check whether the set with to checked stones has the same size as the set that represents all found stones
            // on neighbour positions of own player;
            // 10. --> if true, placed move is captured as a group of stones! If not true, redo from 3 (loop through set).
            if (setOfPositions.size() == setOfCheckedPositions.size()) {
                return true;
            }
        }
        return true;
    }

    /**
     * Removes stone(s) from the board after capturing this (group of) stone(s).
     *
     * @param row    is the row the stone to be removed is positioned
     * @param column is the column the stone to be removed is positioned
     */
    public void removeStone(int row, int column) {
        // not row/column input, but set input, loop through set and get position from this set and remove the stone
        // on that position
        board.removeStone(row, column);
    }

    /**
     * Executes the move when this move is found to be valid. If not valid, the turn stays at this player and he/she
     * needs to try a new move or pass.
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
                System.out.println("This is not a valid move, try again.");// maybe to TUI, and recall doMove with other row/column input. OR pass!
            } else {
                // hasCaptured checked before isCaptured, as a suicide move resulting in capturing a group is allowed
                if (hasCaptured(row, column)) {
                    for (Position position : setOfPositions) {
                        removeStone(position.getRow(), position.getColumn());
                    }
                }
                if (isCaptured(row, column)) {
                    for (Position position : setOfPositions) {
                        removeStone(position.getRow(), position.getColumn());
                    }
                }
                // reset passCount if move is made
                passCount = 0;
                // after making a move, it is the turn of the opponent
                switchTurn();
            }
        }
    }

    /**
     * Passes without placing a stone.
     */
    public void pass() {
        // update passCount: when two consecutive passes are made, the game is over
        passCount++;
        // the turn goes to the opponent without placing a stone
        switchTurn();
    }

    /**
     * Gets the passCounter (number of consecutive passes).
     *
     * @return the number of consecutive passes
     */
    public int getPassCount() {
        return passCount;
    }

    /**
     * Checks whether the game is over.
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
     * Determines the final score of the player.
     *
     * @param player is player of interest
     * @return the final score
     */
    public int finalScore(Player player) {
        // loop through board? Remove all white stones and then ....?
        score = 0;
        return score;
    }

    /**
     * Gets the winner of this game.
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
        game.doMove(0, 2);
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
        game.getBoard().printBoard();
        game.doMove(4, 3); // W
        game.getBoard().printBoard();
        game.doMove(7,7); // B
        game.doMove(1, 1); // W
        game.doMove(7,8); // B
        game.doMove(2, 0); // W
        game.getBoard().printBoard();

    }


// OK A move consists of placing one stone of a player their own color on an empty intersection on the board.
// OK A player may pass their turn at any time.
// OK A stone is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// A solidly connected group of stones of one color is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// OK Self-capture/suicide is allowed (this is different from the commonly used rules). = on each empty field a stone can be placed.
// OK When a suicide move results in capturing a group, the group is removed from the board first and the suiciding stone stays alive.
// OK No stone may be played so as to recreate any previous board position (ko rule https://en.wikipedia.org/wiki/Rules_of_go#Repetition).
// OK Two consecutive passes will end the game.
// OK A player's area consists of all the intersections the player has either occupied or surrounded.
// The player with the biggest area wins. This way of scoring is called Area Scoring
// https://en.wikipedia.org/wiki/Rules_of_go#Area_scoring. In case of an equal score, there is a draw.

// The oldest counting method is as follows: At the end of the game, all white stones are removed from the board, and the players use black stones to fill the entirety of the black territory. Score is determined by counting the black stones. Since the board contains 361 intersections, black must have 181 or more stones to win. This method is still widely used in Mainland China.
//Around 1975, Taiwanese player and industrialist Ing Chang-ki invented a method of counting now known as Ing counting. Each player begins the game with exactly 180 stones (Ing also invented special stone containers that count each player's stones). At the end, all stones are placed on the board. One vacant intersection will remain, appearing in the winner's area; the number of stones of one color in the other color's area will indicate the margin of victory.
}


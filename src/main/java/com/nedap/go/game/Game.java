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

    /**
     * Checks whether the move a player wants to make, is a valid move (whether te stone is placed on an existing
     * position and whether this stone is placed on an empty position).
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return true if the move is valid; false if not
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
     * Checks whether the ko rule is violated (which means, that the current state of the board has been
     * existing before and therefore this move is not a valid move).
     *
     * @param stringRepresentationOfBoard is the String representation of the current board
     * @return true if this state of the board has existed before
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
                System.out.println("Violation of the ko rule: a stone that will recreate a former board position may not be placed)!");
                return true;
            }
        }
        listPreviousBoards.add(stringRepresentationOfBoard);
        return false;
    }

// Methods needed to check whether placed stone is/has captured ((by) a group of) stones:

    /**
     * Gets a set with the positions of the neighbours of the placed or checked stone.
     *
     * @param row    is the row a player has placed a stone, or the row of the checked stone
     * @param column is the column a player has placed a stone, or the column of the checked stone
     * @return a set of positions of the neighbours of the placed or checked stone
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
     * Gets a set with the stones that are located on the neighbour positions.
     *
     * @return a set of stones that are located on the neighbour positions
     */
    public Set<Stone> getNeighbourStones(Set<Position> neighbourPositions) {
        Set<Stone> neighbourStones = new HashSet<>();
        for (Position neighbourPosition : neighbourPositions) {
            Stone neighbourStone = board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn());
            neighbourStones.add(neighbourStone);
        }
        return neighbourStones;
    }

    /**
     * Checks whether the stone to check has a neighbour stone with the same color.
     *
     * @return true if the placed stone has a neighbour of the same color, false if not
     */
    public boolean hasNeighbourOfSameColor(Set<Stone> neighbourStones, Stone stone) {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == stone) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a set for each direct neighbour of the placed stone that contains a stone of the opponent, to be able
     * to check whether these neighbours are part of a group of stones, and to check whether this neighbour stone (or
     * group of stones) is captured by placing this stone. One set per direct opponent neighbour (to be able to check if
     * each neighbour is (part of) a group) is placed in a set that stores these.
     */
    public Set<Set<Position>> createSetOfOpponentGroupPositionSets(Set<Position> neighbourPositions) {
        Set<Set<Position>> opponentGroupPositionsPlacedStone = new HashSet<>();
        for (Position neighbourPosition : neighbourPositions) {
            if (board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn()) == getStoneOpponent(currentPlayer)) {
                Set<Position> groupPositions = new HashSet<>();
                groupPositions.add(neighbourPosition);
                opponentGroupPositionsPlacedStone.add(groupPositions);
            }
        }
        return opponentGroupPositionsPlacedStone;
    }

    /**
     * Creates a new set to be able to keep track of the positions that are checked on their neighbour stones.
     */
    public Set<Position> createSetOfCheckedPositions() {
        Set<Position> checkedPositions = new HashSet<>();
        return checkedPositions;
    }

    /**
     * Checks whether the checked stone has an empty neighbour position.
     *
     * @return true if the checked stone has an empty neighbour position, false if not
     */
    public boolean hasEmptyNeighbours(Set<Stone> neighbourStones) {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == Stone.EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the current player has captured a stone or group of stones of the opponent with the current move,
     * and if it does, this group is removed.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     */
    public void removeIfHasCaptured(int row, int column) {
        // 1.  Find the position of the direct neighbours of the placed stone;
        Set<Position> neighbourPositions = getNeighbourPositions(row, column);
        // 2.  Find the stones located on these neighbour positions;
        Set<Stone> neighbourStones = getNeighbourStones(neighbourPositions);
        // 3.  Check if at least 1 of the neighbour stones is of the opponent; if not, no stone of the opponent is captured
        //      by making this move;
        if (!hasNeighbourOfSameColor(neighbourStones, getStoneOpponent(currentPlayer))) {
            return;
        }
        // 4.  Create a new set for each of the neighbour stones of the opponent and store these in a new set;
        Set<Set<Position>> opponentGroupPositionsPlacedStone = createSetOfOpponentGroupPositionSets(neighbourPositions);
        // 5.  Loop through the set with (maximal four) sets of one position;
        for (Set<Position> positionsOfPossiblyCapturedGroup : opponentGroupPositionsPlacedStone) {
            //TODO als tijd: check if possiblePositionsOfCapturedGroup al onderdeel is van een eerder gecheckte groep;
            // dan hoeft niet verder (geen dubbel check nodig).

            // 6.  Check for each of these positions if they are part of a group that is captured by making this move.
            Set<Position> neighbourGroup = getCapturedGroup(positionsOfPossiblyCapturedGroup);
            // 7.  If the neighbourGroup is not captured, this group is returned as an empty Set of positions. If the
            //      group is captured, the neighbourGroup is a set that stores all positions of this group, and these
            //      will be removed from the board.
            if (!neighbourGroup.isEmpty()) {
                removeGroupOfStones(neighbourGroup);
            }
        }
    }


    /**
     * Gets a set with all the positions of a captured group.
     *
     * @param positionsOfPossiblyCapturedGroup contains the position of the stone that might be part of a group that
     *                                         is captured and now needs to be checked (direct neighbour of a placed
     *                                         stone for hasCaptured and the placed stone itself for isCaptured).
     * @return a set of all positions of a captured group; return an empty set if group is not captured
     */
    private Set<Position> getCapturedGroup(Set<Position> positionsOfPossiblyCapturedGroup) {
        // 1.  Create a new set to store the checked stones;
        Set<Position> checkedPositions = createSetOfCheckedPositions();
        // 2.  Create a new queue for the stones that are of the opponent that need to be checked, and add the position
        //      from the set with one position of a possibly captured group to the queue in order to check this
        //      position on its neighbours;
        Queue<Position> positionsToCheck = new LinkedList<>(positionsOfPossiblyCapturedGroup);
        // 3.  Go into the queue created by 2. Get the first position that is stored in that queue and check the neighbour
        //      positions of that stone;
        while (!positionsToCheck.isEmpty()) {
            Position position = positionsToCheck.poll();
            Set<Position> neighbourPositions = getNeighbourPositions(position.getRow(), position.getColumn());
            // 4.  Find the stones that are located on these neighbour positions;
            Set<Stone> neighbourStones = getNeighbourStones(neighbourPositions);
            // 5.  Check if at least one of those neighbour stones is EMPTY --> no capture! (return an empty set);
            if (hasEmptyNeighbours(neighbourStones)) {
                return new HashSet<>();
            }
            // 6.  Check if any of the neighbours is a stone that is part of the group (has the same color as the stone
            //      that is currently being checked); when this is the case, first check if these stones are already in
            //      the queue with stones to check OR in the set with checked positions; if not: add the stone(s) to
            //      the queue with positions to check as created in 2;
            if (hasNeighbourOfSameColor(neighbourStones, board.getStone(position.getRow(), position.getColumn()))) {
                for (Position neighbourPosition : neighbourPositions) {
                    if (board.getStone(neighbourPosition.getRow(), neighbourPosition.getColumn()) == board.getStone(position.getRow(), position.getColumn())) {
                        if ((!checkedPositions.contains(neighbourPosition)) && (!positionsToCheck.contains(neighbourPosition))) {
                            positionsToCheck.add(neighbourPosition);
                        }
                    }
                }
            }
            // 7.  Add the currently checked position to the set as created by 1;
            checkedPositions.add(position);
            // 8.  Go back into the while loop; if the queue is not empty, redo from 4 up to here. If the queue is empty,
            //      all positions are checked: all neighbours with the same color are found and no empty neighbour position
            //      is found, indicating that the checkedPositions represents the group that is captured.
        }
        return checkedPositions;
    }

    /**
     * Checks whether the current player has self-captured (suicide) a stone or group of stones with this move.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     */
    public void removeIfIsCaptured(int row, int column) {
        // 1.  Create a new set and add the placed stone, to be able to check if it is part of a group and if this stone
        //      (or group of stones) is captured;
        Set<Position> positionsOfPossiblyCapturedGroup = new HashSet<>();
        positionsOfPossiblyCapturedGroup.add(new Position(row, column));
        // 2.  Check whether this position is part of a group that is captured by making this move;
        Set<Position> checkedGroup = getCapturedGroup(positionsOfPossiblyCapturedGroup);
        // 3.  If the checkedGroup is not captured, this group is returned as an empty Set of positions. If the
        //      group is captured, the neighbourGroup is a set that stores all positions of this group, and these
        //      will be removed from the board.
        if (!checkedGroup.isEmpty()) {
            removeGroupOfStones(checkedGroup);
        }
    }

    /**
     * Removes a single stone from the board (ko rule violation).
     *
     * @param row    is the row the stone to be removed is positioned
     * @param column is the column the stone to be removed is positioned
     */
    public void removeStone(int row, int column) {
        board.removeStone(row, column);
    }

    /**
     * Removes a single stone or a group of stones that is captured.
     *
     * @param checkedPositions is the set that contains all positions of the group that needs to be removed.
     */
    public void removeGroupOfStones(Set<Position> checkedPositions) {
        for (Position position : checkedPositions) {
            removeStone(position.getRow(), position.getColumn());
        }
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
            // hasCaptured checked before isCaptured, as a suicide move resulting in capturing a group is allowed:
            removeIfHasCaptured(row, column);
            removeIfIsCaptured(row, column);
            // Check KO rule violation. If this rule is violated, the stone will be removed again and the player can try
            // a new move --> no switch turn.
            if (isKoRuleViolated(getBoard().toString())) {
                board.removeStone(row, column);
                System.out.println("This is not a valid move, try again.");// maybe to TUI, and recall doMove with other row/column input. OR pass!
            } else {
                // reset passCount if valid move is executed
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
        int score = 0;
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
}


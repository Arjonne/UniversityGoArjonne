package com.nedap.go.game;

import java.util.*;

/**
 * Represents the GO game, including rules.
 */

public class Game {
    private Player playerBlack;
    private Player playerWhite;
    private Board board;
    private GoGUI goGUI;
    private Player currentPlayer;
    private static int passCount; //todo probably static, but check if it works when game is finished!
    private List<String> listPreviousBoards;
    private Set<Position> emptyPositions;

    /**
     * Creates a new game with two players, a board and the GUI representation of the board.
     *
     * @param playerBlack player with black stones;
     * @param playerWhite player with white stones;
     * @param board       the game board.
     */
    public Game(Player playerBlack, Player playerWhite, Board board, GoGUI goGUI) {
        this.playerBlack = playerBlack;
        this.playerWhite = playerWhite;
        this.board = board;
        this.goGUI = goGUI;
        // as Black always starts the game, this player is assigned to currentPlayer in the constructor
        currentPlayer = playerBlack;
        // each game starts with a pass count of 0 (after two consecutive passes, the game is over)
        passCount = 0;
        // create a list to store all previous states of the board (which are represented as a String) to be able to
        // check the ko rule
        listPreviousBoards = new ArrayList<>();
        // create a new set to keep track of all empty positions on the board. As all positions are empty at the start
        // of the game, all positions are added to this set by creating it.
        createEmptyPositionSet();
    }

    // Getters:

    /**
     * Gets the board.
     *
     * @return the board of the game.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the stone of the current player.
     *
     * @param currentPlayer is the player that currently can make a move or pass;
     * @return the stone this player uses.
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
     * @param currentPlayer is the player that currently can make a move or pass;
     * @return the stone the opponent player uses.
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
     * @return the player that can make a move or pass (either playerBlack or playerWhite).
     */
    public Player getCurrentPlayer() {
        if (currentPlayer == playerBlack) {
            return playerBlack;
        } else {
            return playerWhite;
        }
    }

    // Methods needed to determine a (random) valid move:

    /**
     * Creates a set that stores all positions on which no stone is placed. As all positions are empty at the start of
     * the game, all positions are added to this set by creating it.
     */
    public void createEmptyPositionSet() {
        emptyPositions = new HashSet<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position position = new Position(row, column);
                emptyPositions.add(position);
            }
        }
    }

    /**
     * Gets the set with all positions on which no stone is placed.
     *
     * @return the complete set with empty positions.
     */
    public Set<Position> getEmptyPositions() {
        return emptyPositions;
    }

    /**
     * Gets a list of valid position based on random empty positions on the board that do not violate the ko rule.
     *
     * @return the list of valid positions.
     */
    public List<Position> getListOfValidPositions() {
        // First, get the set of empty positions:
        Set<Position> setOfEmptyPositions = getEmptyPositions();
        // Create a new list to be able to store the valid positions. Use a list, so that it is possible to get a
        // random index of that list and place a stone on the position that is stored on that random index:
        List<Position> listOfValidPositions = new ArrayList<>();
        // loop through all positions as stored in the set of empty positions, place a stone on each position on a copy
        // of the board to be able to check whether this move is a valid move (stone placed on an empty position, on a
        // position that actually exists on the board and whether the ko rule is not violated by placing this stone).
        // If the move is a valid move, add the position to the list of valid positions.
        for (Position position : setOfEmptyPositions) {
            if (isValidMove(position.getRow(), position.getColumn())) {
                listOfValidPositions.add(position);
            }
        }
        return listOfValidPositions;
    }

    /**
     * Finds a valid position based on a random empty position on the board that does not violate the ko rule.
     *
     * @return the position of the random valid move. Can be null if no valid position is available.
     */
    public Position findRandomValidPosition() {
        // to be able to find a random valid position, the game must not be over yet, and the list of valid positions
        // must not be empty.
        if (!isGameOver() && !getListOfValidPositions().isEmpty()) {
            List<Position> listOfValidPositions = getListOfValidPositions();
            // to be able to make a random move (on a random valid position), get a random index of the list with
            // valid positions and get the position that is stored on that index.
            int randomValidIndex = (int) (Math.random() * listOfValidPositions.size());
            return listOfValidPositions.get(randomValidIndex);
        } else {
            return null;
        }
    }

    /**
     * Checks whether the move a player wants to make, is a valid move (whether te stone is placed on an existing
     * position and whether this stone is placed on an empty position).
     *
     * @param row    is the row a player wants to place a stone;
     * @param column is the column a player wants to place a stone;
     * @return true if the move is valid; false if not.
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
        // check if placed stone will result in violating ko rule by placing this stone on a copy of the current board
        // (this way, you don't have to replace all captured stones if ko rule is violated and move must be undone).
        Board copyBoard = board.copyBoard();
        copyBoard.placeStone(row, column, getStone(currentPlayer));
        removeIfIsCaptured(row, column);
        removeIfHasCaptured(row, column);
        return !isKoRuleViolated(copyBoard.toString());
    }

    /**
     * Checks whether the ko rule is violated (which means, that the current state of the board has been
     * existing before and therefore this move is not a valid move).
     *
     * @param stringRepresentationOfBoard is the String representation of the current board;
     * @return true if this state of the board has existed before.
     */
    public boolean isKoRuleViolated(String stringRepresentationOfBoard) {
        // loop through the list with Strings (in which all previous board states are saved) and compare the current
        // state of the board with all previous states
        for (String stringRepresentationPreviousBoardStates : listPreviousBoards) {
            if (stringRepresentationPreviousBoardStates.equals(stringRepresentationOfBoard)) {
                System.out.println("Violation of the ko rule: a stone that will recreate a former board position may not be placed!");
                return true;
            }
        }
        return false;
    }

// Methods needed to check whether a placed stone is captured by (a group of) stone(s) or has captured (a group of)
// stone(s):

    /**
     * Gets a set with the positions of the neighbours of the placed or checked stone.
     *
     * @param row    is the row a player has placed a stone, or the row of the checked stone;
     * @param column is the column a player has placed a stone, or the column of the checked stone;
     * @return a set of positions of the neighbours of the placed or checked stone.
     */
    public Set<Position> getNeighbourPositions(int row, int column) {
        Set<Position> neighbourPositions = new HashSet<>();
        if (row != 0) {
            neighbourPositions.add(new Position((row - 1), column));
        }
        if (row != (Board.SIZE - 1)) {
            neighbourPositions.add(new Position((row + 1), column));
        }
        if (column != 0) {
            neighbourPositions.add(new Position(row, (column - 1)));
        }
        if (column != (Board.SIZE - 1)) {
            neighbourPositions.add(new Position(row, (column + 1)));
        }
        return neighbourPositions;
    }

    /**
     * Gets a set with the stones that are located on the neighbour positions.
     *
     * @return a set of stones that are located on the neighbour positions.
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
     * @return true if the placed stone has a neighbour of the same color, false if not.
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
        return new HashSet<>();
    }

    /**
     * Checks whether the checked stone has a neighbour that results in no capture (during the game: an empty neighbour
     * ensures no capture, for the final score count, a neighbour stone of the opponent results in no capture).
     *
     * @return true if the checked stone has a neighbour stone that results in no capture, false if not.
     */
    public boolean hasNeighbourThatEnsuresNoCapture(Set<Stone> neighbourStones, Stone stoneResultingInNoCapture) {
        for (Stone neighbourStone : neighbourStones) {
            if (neighbourStone == stoneResultingInNoCapture) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the current player has captured a stone or group of stones of the opponent with the current move,
     * and if it does, this group is removed.
     *
     * @param row    is the row a player wants to place a stone;
     * @param column is the column a player wants to place a stone.
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
            // 6.  Check for each of these positions if they are part of a group that is captured by making this move.
            Set<Position> neighbourGroup = getCapturedGroup(positionsOfPossiblyCapturedGroup, Stone.EMPTY);
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
     *                                         stone for hasCaptured and the placed stone itself for isCaptured);
     * @param stoneResultingInNoCapture        is the neighbour stone that directly ensures that there is no capture
     *                                         (which is an EMPTY position during the game, and the stone of the
     *                                         opponent during the count of the final score);
     * @return a set of all positions of a captured group; return an empty set if group is not captured.
     */
    private Set<Position> getCapturedGroup(Set<Position> positionsOfPossiblyCapturedGroup, Stone stoneResultingInNoCapture) {
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
            // 5.  For isCaptured and hasCaptured: check if at least one of those neighbour stones is EMPTY -->
            //      no capture! For counting the final score, check whether a group of EMPTY stones has a neighbour of
            //      the opponent --> no capture! (in all cases: return an empty set);
            if (hasNeighbourThatEnsuresNoCapture(neighbourStones, stoneResultingInNoCapture)) {
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
     * @param row    is the row a player wants to place a stone;
     * @param column is the column a player wants to place a stone.
     */
    public void removeIfIsCaptured(int row, int column) {
        // 1.  Create a new set and add the placed stone, to be able to check if it is part of a group and if this stone
        //      (or group of stones) is captured;
        Set<Position> positionsOfPossiblyCapturedGroup = new HashSet<>();
        positionsOfPossiblyCapturedGroup.add(new Position(row, column));
        // 2.  Check whether this position is part of a group that is captured by making this move;
        Set<Position> checkedGroup = getCapturedGroup(positionsOfPossiblyCapturedGroup, Stone.EMPTY);
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
     * @param row    is the row the stone to be removed is positioned;
     * @param column is the column the stone to be removed is positioned.
     */
    public void removeStone(int row, int column) {
        emptyPositions.add(new Position(row, column));
        board.removeStone(row, column);
        goGUI.removeStone(column, row);
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

    // Methods needed to play the game:

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
     * Executes the move when this move is found to be valid. If not valid, the turn stays at this player and he/she
     * needs to try a new move or pass.
     *
     * @param row    is the row a player wants to place a stone;
     * @param column is the column a player wants to place a stone.
     */
    public void doMove(int row, int column) {
        if (isValidMove(row, column)) {
            board.placeStone(row, column, getStone(currentPlayer));
            goGUI.placeStone(column, row, getStone(currentPlayer));
            // hasCaptured checked before isCaptured, as a suicide move resulting in capturing a group is allowed:
            removeIfHasCaptured(row, column);
            removeIfIsCaptured(row, column);
            // reset passCount if valid move is executed
            passCount = 0;
            // removes the position from the set of emptyPositions as this position is not empty anymore after
            // placing the stone
            emptyPositions.remove(new Position(row, column));
            // add the new board state to the list of previous board states in order to be able to check the ko rule.
            listPreviousBoards.add(board.toString());
            // after making a move, it is the turn of the opponent
            switchTurn();
        } else {
            System.out.println("This is not a valid move, try again."); // in TUI and recall doMove with other row/column
            // input OR pass.
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

    // Methods needed to define whether the game is over:

    /**
     * Gets the passCounter (number of consecutive passes).
     *
     * @return the number of consecutive passes.
     */
    public int getPassCount() {
        return passCount;
    }

    /**
     * Checks whether the game is over.
     *
     * @return true if two consecutive passes are done OR all positions on the board are filled, otherwise false.
     */
    public boolean isGameOver() {
        return passCount == 2 || getBoard().isFull();
    }

    // Methods needed for calculating the final score and find the winner of the game:

    /**
     * Gets the char representation of the player on the board.
     *
     * @param player is the player of interest;
     * @return the char representation of the player, which is 'B' for playerBlack and 'W' for playerWhite.
     */
    public char getCharOfPlayer(Player player) {
        char charLetter;
        if (player == playerBlack) {
            charLetter = 'B';
        } else {
            charLetter = 'W';
        }
        return charLetter;
    }

    /**
     * Gets th first part of the final score: the total number of stones that is located on the final board.
     *
     * @param player                      is the player whose stones on the board are counted;
     * @param stringRepresentationOfBoard is the string representation of the board that is used to get the number of
     *                                    stones that are located on the final state of the board;
     * @return the total number of stones located on the board for the player of interest.
     */
    public int getNumberOfStones(Player player, String stringRepresentationOfBoard) {
        int numberOfStones = 0;
        char charLetter = getCharOfPlayer(player);
        for (int i = 0; i < stringRepresentationOfBoard.length(); i++) {
            if (stringRepresentationOfBoard.charAt(i) == charLetter) {
                numberOfStones++;
            }
        }
        return numberOfStones;
    }

    /**
     * Calculates the score based on captured positions by checking all empty positions on being captured by one player.
     *
     * @param emptyPositions is the set of empty positions that should be checked on whether it is captured by one of
     *                       the players to be able to calculate the final score;
     * @param player         is the player whose score is being calculated;
     * @return the number of captured positions.
     */
    public int scoreBasedOnCapturedPositions(Set<Position> emptyPositions, Player player) {
        // 1.  create a new set to be able to store all empty positions that are captured by a player (use a set to prevent
        //      storing the same position twice).
        Set<Position> positionsOfCaptures = new HashSet<>();
        // 2.  create a new queue with all empty positions to be able to remove checked positions and prevent double-checking.
        Queue<Position> emptyPositionsQueue = new LinkedList<>(emptyPositions);
        // 3.  check all positions in the queue and check whether this position is captured, or part of a group of empty
        //      positions that is being captured by the player of interest.
        while (!emptyPositionsQueue.isEmpty()) {
            Position position = emptyPositionsQueue.poll();
            Set<Position> positionsOfPossiblyCapturedGroup = new HashSet<>();
            positionsOfPossiblyCapturedGroup.add(position);
            Set<Position> checkedGroup = getCapturedGroup(positionsOfPossiblyCapturedGroup, getStoneOpponent(player));
            // 4.  if the position is actually (part of a group that is) captured, this position is added to the set with
            //      captured positions. Besides that, the queue is checked on containing position(s) of the captured (group
            //      of) stone(s). If positions have overlap, these positions are removed from the queue to prevent
            //      double-checking:
            if (!checkedGroup.isEmpty()) {
                positionsOfCaptures.addAll(checkedGroup);
                for (Position checkedPosition : checkedGroup) {
                    emptyPositionsQueue.remove(checkedPosition);
                }
            }
        }
        // 5.  the final count of captures is equal to the number of positions that is stored in the set:
        return positionsOfCaptures.size();
    }

    /**
     * Gets the final score based on the captured positions on the board.
     *
     * @param player is the player the final score based on captured positions;
     * @return the number of captured positions on the board.
     */
    public int getFinalCapturedPositions(Player player) {
        return scoreBasedOnCapturedPositions(emptyPositions, player);
    }

    /**
     * Calculates the final score of the player.
     *
     * @param player is player of interest;
     * @return the final score.
     */
    public int finalScore(Player player) {
        int stonesOnBoard = getNumberOfStones(player, board.toString());
        int capturedPositions = getFinalCapturedPositions(player);
        return stonesOnBoard + capturedPositions;
    }

    /**
     * Gets the winner of this game.
     *
     * @return the player with the most points; can be null if the game ended in a draw.
     */
    public String getWinner() {
        if (finalScore(playerBlack) > finalScore(playerWhite)) {
            return playerBlack.getUsername();
        } else if (finalScore(playerBlack) < finalScore(playerWhite)) {
            return playerWhite.getUsername();
        } else {
            System.out.println("This game ended in a draw!");
            return null;
        }
    }
}


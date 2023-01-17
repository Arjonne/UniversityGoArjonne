package com.nedap.go.game;

/**
 * Represents the GO game, including rules.
 */

public class Game {
    private Player playerBlack;
    private Player playerWhite;
    private Board board;
    private Player currentPlayer;
    private static int passCount;
    private int row;
    private int column;
    private int[][] position = new int[row][column];

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
        // as Black always starts the game, this player is assigned to currentPlayer
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
        }
        currentPlayer = playerBlack;
    }

    public boolean isValidMove() {
        return false;
    }

    public void doMove(int row, int column, Stone stone) {
    }

    public void pass() {
        passCount++;
        switchTurn();
    }

    public boolean isGameOver() { // add something with QUIT?
        if (passCount == 2) {
            return true;
        }
        return false;
    }

    public int finalScore() {
        return 0;
    }

    public Player getWinner() {
        return currentPlayer;
    }




// A move consists of placing one stone of a player their own color on an empty intersection on the board.
// A player may pass their turn at any time.
// A stone or solidly connected group of stones of one color is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
// Self-capture/suicide is allowed (this is different from the commonly used rules).
// When a suicide move results in capturing a group, the group is removed from the board first and the suiciding stone stays alive.
// No stone may be played so as to recreate any previous board position (ko rule https://en.wikipedia.org/wiki/Rules_of_go#Repetition).
// Two consecutive passes will end the game.
// A player's area consists of all the intersections the player has either occupied or surrounded.
// The player with the biggest area wins. This way of scoring is called Area Scoring
// https://en.wikipedia.org/wiki/Rules_of_go#Area_scoring. In case of an equal score, there is a draw.

    // The oldest counting method is as follows: At the end of the game, all white stones are removed from the board, and the players use black stones to fill the entirety of the black territory. Score is determined by counting the black stones. Since the board contains 361 intersections, black must have 181 or more stones to win. This method is still widely used in Mainland China.
    //Around 1975, Taiwanese player and industrialist Ing Chang-ki invented a method of counting now known as Ing counting. Each player begins the game with exactly 180 stones (Ing also invented special stone containers that count each player's stones). At the end, all stones are placed on the board. One vacant intersection will remain, appearing in the winner's area; the number of stones of one color in the other color's area will indicate the margin of victory.
}
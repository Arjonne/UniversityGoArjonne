package com.nedap.go.server;

import com.nedap.go.game.*;

/**
 * Represents a gameHandler of the server for handling the game-related input from two connected clients.
 */

public class GoGameHandler {
    private static ClientHandler clientHandler1;
    private static ClientHandler clientHandler2;
    private static Game game;


    /**
     * Creates a gameHandler to be able to process all game and its rules related information from two clients connected
     * to the server via the clientHandlers that are connected to this gameHandler.
     *
     * @param clientHandler1 is the clientHandler from the first player using the connected client;
     * @param clientHandler2 is the clientHandler from the second player using the connected client.
     */
    public GoGameHandler(ClientHandler clientHandler1, ClientHandler clientHandler2) {
        this.clientHandler1 = clientHandler1;
        this.clientHandler2 = clientHandler2;
    }

    /**
     * Creates a new GO game, with two players, a board and a GUI representation of the board. To be able to define
     */
    public void createNewGame() {
        Player playerBlack = new Player(clientHandler1.getUsername(), Stone.BLACK);
        Player playerWhite = new Player(clientHandler2.getUsername(), Stone.WHITE);
        Board board = new Board();
        GoGUI goGUI = new GoGUI(Board.SIZE);
        game = new Game(playerBlack, playerWhite, board, goGUI);
        clientHandler1.sendYourTurn();
    }

    /**
     * Performs a check on the reference board whether the move a player suggested is a valid move.
     *
     * @param row    is the row this player wants to place a stone;
     * @param column is the column this player wants to place a stone;
     * @return true if it is a valid move, false if not.
     */
    public boolean checkIfMoveIsValid(int row, int column) {
        return game.isValidMove(row, column);
    }

    /**
     * Performs the move on the reference board to keep track of the current state of the game.
     *
     * @param row    is the row this player wants to place a stone;
     * @param column is the column this player wants to place a stone.
     */
    public void doMoveOnReferenceBoard(int row, int column) {
        game.doMove(row, column);
    }

    /**
     * Passes in the reference game to keep track of the current state of the game.
     */
    public void passInReferenceGame() {
        game.pass();
    }

    /**
     * Checks whether the game is over based on the information in this reference game.
     *
     * @return true if the game is over (in case of two consecutive passes or a full board), false if not.
     */
    public boolean checkOnGameOver() {
        return game.isGameOver();
    }

    /**
     * Gets the winner based on this reference game.
     *
     * @return the winner of the game (which can be 'draw' in case of a draw).
     */
    public String getWinner() {
        return game.getWinner();
    }

}

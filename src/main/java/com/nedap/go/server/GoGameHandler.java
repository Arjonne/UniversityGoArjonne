package com.nedap.go.server;

import com.nedap.go.game.*;
import jdk.swing.interop.SwingInterOpUtils;

/**
 * Represents a gameHandler of the server for handling the game-related input from two connected clients.
 */
public class GoGameHandler implements Runnable {
    private ClientHandler clientHandler1;
    private ClientHandler clientHandler2;
    private Game game;
    private Player playerBlack;
    private Player playerWhite;
    private boolean wantsToCheckIfMoveIsValid;
    private boolean wantsToPassInReferenceGame;
    private boolean wantsToCheckForGameOver;
    private boolean hasQuited;
    private boolean quit;
    private int row;
    private int column;
    public static final String DISCONNECT = "DISCONNECT";
    public static final String VICTORY = "VICTORY";


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
        createNewGame();
    }

    /**
     * Runs this operation. As long as the game is not over, the gameHandler waits on input from the clientHandler to
     * do checks on the rules and keep track of the game. When the game is over, it will return the winner.
     */
    @Override
    public void run() {
        quit = false;
        while (!quit) {
            // built sleep in as the loop does not work otherwise...
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Not able to wait for 2 seconds.");
            }
            if (wantsToCheckIfMoveIsValid) {
                checkIfMoveIsValid(row, column);
            } else if (wantsToPassInReferenceGame) {
                passInReferenceGame();
            } else if (wantsToCheckForGameOver) {
                checkOnGameOver();
            } else if (hasQuited) {
                processQuit();
            }
        }
        System.out.println("This game has been ended. You can close the board.");
    }

    // Methods that the go can use to get or set the state of a boolean in order to invoke a specific method:

    /**
     * Changes the boolean to be able to know whether the position should be checked on validity now.
     *
     * @param wantsToCheckIfMoveIsValid is the boolean that represents whether a position should be checked on being a
     *                                  valid move or not.
     */
    public void setWantsToCheckIfMoveIsValid(boolean wantsToCheckIfMoveIsValid, int row, int column) {
        this.row = row;
        this.column = column;
        this.wantsToCheckIfMoveIsValid = wantsToCheckIfMoveIsValid;
    }

    /**
     * Changes the boolean to be able to know whether a pass should be processed now.
     *
     * @param wantsToPassInReferenceGame is the boolean that represents whether a player wants to pass or not.
     */
    public void setWantsToPassInReferenceGame(boolean wantsToPassInReferenceGame) {
        this.wantsToPassInReferenceGame = wantsToPassInReferenceGame;
    }

    /**
     * Changes the boolean to be able to know whether a quit message should be processed now.
     *
     * @param hasQuited is the boolean that represents whether a player wants to quit or not.
     */
    public void setHasQuited(boolean hasQuited) {
        this.hasQuited = hasQuited;
    }

    // Methods that can be called by the clientHandler by changing the state of a boolean:

    /**
     * Creates a new GO game, with two players and a board (the gameHandler on the server side does not need to have a
     * GUI representation of the board).
     */
    public void createNewGame() {
        playerBlack = new Player(clientHandler1.getUsername(), Stone.BLACK);
        playerWhite = new Player(clientHandler2.getUsername(), Stone.WHITE);
        Board board = new Board();
        GoGUI goGUI = new GoGUI(Board.SIZE);
        game = new Game(playerBlack, playerWhite, board, goGUI);
        clientHandler1.sendNewGame(clientHandler1.getUsername(), clientHandler2.getUsername());
        clientHandler2.sendNewGame(clientHandler1.getUsername(), clientHandler2.getUsername());
        // As player black always starts with the game, send yourTurn to this player:
        clientHandler1.sendYourTurn();
    }

    /**
     * Performs a check on the reference board whether the move a player suggested is a valid move. If this move is a
     * valid move, it is placed on the reference board to be able to keep track of the current game state on the server
     * side, and a message is sent to the players using the clients connected to the clientHandlers of the move to be
     * played (or that the move is invalid to the player whose move is invalid).
     *
     * @param row    is the row this player wants to place a stone;
     * @param column is the column this player wants to place a stone;
     */
    public void checkIfMoveIsValid(int row, int column) {
        if (!game.isValidMove(row, column)) {
            if (game.getCurrentPlayer() == playerBlack) {
                clientHandler1.sendInvalidMove();
            } else {
                clientHandler2.sendInvalidMove();
            }
        } else {
            clientHandler1.sendMove(game.getCurrentPlayer().getUsername(), row, column);
            clientHandler2.sendMove(game.getCurrentPlayer().getUsername(), row, column);
            game.doMove(row, column);
        }
        wantsToCheckIfMoveIsValid = false;
        wantsToCheckForGameOver = true;
    }

    /**
     * Passes in the reference game to keep track of the current state of the game, and sends the message to the players
     * using the clients connected to the clientHandlers of a pass.
     */
    public void passInReferenceGame() {
        clientHandler1.sendPass(game.getCurrentPlayer().getUsername());
        clientHandler2.sendPass(game.getCurrentPlayer().getUsername());
        game.pass();
        wantsToPassInReferenceGame = false;
        wantsToCheckForGameOver = true;
    }

    /**
     * Checks whether the game is over based on the information in this reference game. If the game is not over, the
     * player whose turn it is next will be informed. Otherwise, a message will be sent that the game is over and the
     * winner will be shown.
     */
    public void checkOnGameOver() {
        if (game.isGameOver()) {
            clientHandler1.sendGameOver(VICTORY, game.getWinner());
            clientHandler2.sendGameOver(VICTORY, game.getWinner());
            quit = true;
        } else {
            if (game.getCurrentPlayer() == playerBlack) {
                clientHandler1.sendYourTurn();
            } else {
                clientHandler2.sendYourTurn();
            }
        }
        wantsToCheckForGameOver = false;
    }

    /**
     * Processes the quit message that is received if a player has quited during playing a game. A message will be sent
     * to the player that is still in the game.
     */
    public void processQuit() {
        if (game.getCurrentPlayer() == playerBlack) {
            clientHandler1.sendGameOver(DISCONNECT, clientHandler2.getUsername());
        } else {
            clientHandler2.sendGameOver(DISCONNECT, clientHandler1.getUsername());
        }
        hasQuited = false;
        quit = true;
    }
}

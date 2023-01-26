package com.nedap.go;

public final class Protocol {
    private Protocol() {
    }

    public static final String SEPARATOR = "~";
    // Server-side
    public static final String WELCOME = "WELCOME";
    public static final String USERNAMETAKEN = "USERNAMETAKEN";
    public static final String JOINED = "JOINED";
    public static final String NEWGAME = "NEWGAME";
    public static final String GAMEOVER = "GAMEOVER";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String VICTORY = "VICTORY";
    public static final String YOURTURN = "YOURTURN";
    public static final String INVALIDMOVE = "INVALIDMOVE";

    // Client-side
    public static final String HELLO = "HELLO";
    public static final String USERNAME = "USERNAME";
    public static final String QUEUE = "QUEUE";
    public static final String PASS = "PASS";
    public static final String QUIT = "QUIT";

    // both sides
    public static final String MOVE = "MOVE"; // both in server and client side?  username played this move || username passed
    public static final String ERROR = "ERROR";

    /**
     * Builds a new protocol message which instructs the server that you want to connect.
     * @param clientDescription is the description of the client
     * @return the description of the server in the correct format
     */
    public static String helloMessage(String clientDescription) {
        return HELLO + SEPARATOR + clientDescription;
    }

    /**
     * Builds a new protocol message which instructs the client that the server wants to accept the connection.
     *
     * @param serverDescription is the description of the server
     * @return the description of the client in the correct format
     */
    public static String welcomeMessage(String serverDescription) {
        return WELCOME + SEPARATOR + serverDescription;
    }

    /**
     * Builds a new protocol message which instructs the server what username the client wants to use.
     *
     * @param username is the username the client wants to use
     * @return the username in the correct format
     */
    public static String username(String username) {
        return USERNAME + SEPARATOR + username;
    }

    /**
     * Builds a new protocol message which instructs the client that the connection is correctly established.
     *
     * @param message is the message the server returns
     * @return the message in the correct format
     */
    public static String joined(String message) {
        return JOINED + SEPARATOR + message;
    }

    /**
     * Builds a new protocol message which instructs the client that the username is already taken. This only needs
     * to be sent to the player who has tried to use this username.
     *
     * @param message is the message the server returns
     * @return the message in the correct format
     */
    public static String usernameTaken(String message) {
        return USERNAMETAKEN + SEPARATOR + message;
    }

    /**
     * Builds a new protocol command which instructs the server that the client wants to participate in playing GO.
     *
     * @return the queue message in the correct format
     */
    public static String queue() {
        return QUEUE;
    }

    /**
     * Builds a new protocol command which instructs the server that the client wants to participate in playing GO.
     *
     * @param username1 is the username of player 1
     * @param username2 is the username of player 2
     * @return the two players that are starting this new game in the correct format
     */
    public static String newGame(String username1, String username2) {
        return NEWGAME + SEPARATOR + username1 + SEPARATOR + username2;
    }

    /**
     * Builds a new protocol command which instructs the server which move a player wants to make.
     *
     * @param row    is the row a player wants to place a stone
     * @param column is the column a player wants to place a stone
     * @return the move a player wants to make in the correct format
     */
    public static String move(int row, int column) {
        return MOVE + SEPARATOR + row + SEPARATOR + column;
    }

    /**
     * Builds a new protocol command which instructs the clients which move was made by which player.
     *
     * @param username is the username of the player that made the move
     * @param row      is the row this player placed a stone
     * @param column   is the column this player placed a stone
     * @return the move that is made by this player in the correct format
     */
    public static String move(String username, int row, int column) {
        return MOVE + SEPARATOR + username + SEPARATOR + row + SEPARATOR + column;
    }

    /**
     * Builds a new protocol message which instructs the client that the move is an invalid move. This only needs
     * to be sent to the player who has tried to do this move.
     *
     * @return the message of the invalid move in the correct format
     */
    public static String invalidMove() {
        return INVALIDMOVE;
    }

    /**
     * Builds a new protocol command which instructs the server that a player wants to pass.
     *
     * @return the pass message in the correct format
     */
    public static String pass() {
        return PASS;
    }

    /**
     * Builds a new protocol command which instructs the clients that a player has passed.
     *
     * @param username is the username of the player that has passed
     * @return the pass message in the correct format
     */
    public static String pass(String username) {
        return MOVE + username + PASS;
    }

    /**
     * Builds a new protocol command which instructs the client that it is his/her turn.
     *
     * @return the message in the correct format
     */
    public static String yourTurn() {
        return YOURTURN;
    }

    /**
     * Builds a new protocol message which instructs the server that a client has quit.
     *
     * @return the message in the correct format
     */
    public static String quit() {
        return QUIT;
    }

    public static String gameOver(String reason, String usernameWinner) {
        if (reason.equals(DISCONNECT)) {
            return GAMEOVER + SEPARATOR + DISCONNECT + SEPARATOR + usernameWinner;
        } else {
            return GAMEOVER + SEPARATOR + VICTORY + SEPARATOR + usernameWinner;
        }
    }

    public static String error(String message) {
        return ERROR + SEPARATOR + message;
    }
}
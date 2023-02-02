package com.nedap.go.server;

import com.nedap.go.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents a clientHandler of the server for a connected client.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private PrintWriter writerToClient;
    private BufferedReader inputFromClient;
    private String usernameStored;
    public static final String HELLO = "HELLO";
    public static final String USERNAME = "USERNAME";
    public static final String QUEUE = "QUEUE";
    public static final String MOVE = "MOVE";
    public static final String PASS = "PASS";
    public static final String QUIT = "QUIT";
    public static final String SEPARATOR = "~";

    /**
     * Creates a clientHandler to be able to handle the input from the client that is connected to the server.
     *
     * @param socket is the client socket that is needed to establish a connection between server and client
     * @param server is the server a client is connected to
     */
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            writerToClient = new PrintWriter(socket.getOutputStream(), true);
            inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread clientHandlerThread = new Thread(this);
            clientHandlerThread.start();
        } catch (IOException e) {
            System.out.println("Connection with the server could not be established.");
        }
    }

    // Methods needed to use the clientHandler:

    /**
     * Closes the clientHandler. To do so, first the client socket is closed, and both the printWriter and
     * bufferedReader are closed too to prevent leakage. Besides, the server removes this clientHandler from the
     * list of connected clients, it removes the username from the list of usernames and if the client connected to
     * this clientHandler was in the queue for playing the game, it is removed from the queue as well.
     */
    public void close() {
        try {
            socket.close();
            writerToClient.close();
            inputFromClient.close();
            server.removeClientHandler(this);
            server.removeUsername(getUsername());
            if (server.getWaitingQueue().contains(this)) {
                server.removeFromQueue(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs this operation. As long as the socket is not closed (= as long as the client is connected to the server),
     * the input from the client can be processed. Input comes in from and goes back to the clientHandler via
     * predefined format as described in the protocol.
     */
    @Override
    public void run() {
        int queueCount = 0;
        while (!socket.isClosed()) {
            try {
                String clientInput = inputFromClient.readLine();
                if (clientInput == null) {
                    close();
                    break;
                }
                String[] split = clientInput.split(SEPARATOR);
                String command = split[0];
                switch (command) {
                    case HELLO:
                        System.out.println(clientInput);
                        sendWelcome("Server by Arjonne");
                        break;
                    case USERNAME:
                        String username = split[1];
                        createUsername(username, clientInput);
                        break;
                    case QUEUE:
                        queueCount++;
                        if (queueCount % 2 != 0) {
                            enterQueue(clientInput);
                            System.out.println(getUsername() + " has successfully entered the queue. Waiting for a second player....");
                            server.createNewGame();
                        } else {
                            leaveQueue();
                        }
                        break;
                    case MOVE:
                        int row = Integer.parseInt(split[1]);
                        int column = Integer.parseInt(split[2]);
                        server.getGoGameHandler(this).setWantsToCheckIfMoveIsValid(true, row, column);
                        break;
                    case PASS:
                        server.getGoGameHandler(this).setWantsToPassInReferenceGame(true);
                        break;
                    case QUIT:
                        queueCount = 0;
                        server.getGoGameHandler(this).setHasQuited(true);
                        close();
                        break;
                    default:
                        System.out.println("The input is not correct.");
                        break;
                }
            } catch (IOException e) {
                close();
                break;
            }
        }
    }

    // Methods needed to send the messages to the client:

    /**
     * Sends the welcome message as part of the handshake in the correct format to the client. Besides that,
     * the formatted message is visible in the server TUI.
     *
     * @param serverID is the ID of the server.
     */
    public void sendWelcome(String serverID) {
        String serverIdFormatted = Protocol.welcomeMessage(serverID);
        writerToClient.println(serverIdFormatted);
        System.out.println(serverIdFormatted);
    }

    /**
     * Sends a message that the entered username is already taken as part of the handshake in the correct format to the
     * clientHandler. Besides that, the formatted message is visible in the server TUI.
     *
     * @param message is the message that describes that the username is not accepted as it is already in use.
     */
    public void sendUsernameTaken(String message) {
        String messageFormatted = Protocol.usernameTaken(message);
        writerToClient.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Sends a message that the client is correctly connected to the server as part of the handshake in the correct
     * format to the clientHandler. Besides that, the formatted message is visible in the server TUI.
     *
     * @param message is the message with description of the correct connection for the client.
     */
    public void sendJoined(String message) {
        String messageFormatted = Protocol.joined(message);
        writerToClient.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Sends a message to the client that a new game is started in the correct format to the clientHandler. Besides
     * that, the formatted message is visible in the server TUI.
     */
    public void sendNewGame(String username1, String username2) {
        String messageFormatted = Protocol.newGame(username1, username2);
        writerToClient.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Sends a message to the client whose turn it is in the correct format to the clientHandler. Besides
     * that, the formatted message is visible in the server TUI.
     */
    public void sendYourTurn() {
        String yourTurn = Protocol.yourTurn();
        writerToClient.println(yourTurn);
        System.out.println(yourTurn);
    }

    /**
     * Sends a message to both clients with which client made what move in the correct format. Besides that, the
     * formatted message is visible in the server TUI.
     *
     * @param username is the username of the client that made this move;
     * @param row      is the row of the position of this move;
     * @param column   is the column of the position of this move.
     */
    public void sendMove(String username, int row, int column) {
        String moveFormatted = Protocol.move(username, row, column);
        writerToClient.println(moveFormatted);
        System.out.println(moveFormatted);
    }

    /**
     * Sends a message to both clients with which client passed in the correct format to the client. Besides
     * that, the formatted message is visible in the server TUI.
     *
     * @param username is the username of the client that passed.
     */
    public void sendPass(String username) {
        String passFormatted = Protocol.pass(username);
        writerToClient.println(passFormatted);
        System.out.println(passFormatted);
    }

    /**
     * Sends a message to the client that an invalid move was made in the correct format. This message was only sent to
     * the client that made this invalid move. Besides that, the formatted message is visible in the server TUI.
     */
    public void sendInvalidMove() {
        String invalidMoveFormatted = Protocol.invalidMove();
        writerToClient.println(invalidMoveFormatted);
        System.out.println(invalidMoveFormatted);
    }

    /**
     * Sends a message to both clients that the game is over in the correct format to the clientHandler. This message is
     * sent to both participating clients. Besides that, the formatted message is visible in the server TUI.
     */
    public void sendGameOver(String reason, String usernameWinner) {
        String gameOverFormatted = Protocol.gameOver(reason, usernameWinner);
        writerToClient.println(gameOverFormatted);
        System.out.println(gameOverFormatted);
    }

    // Methods needed to process information received from the client:

    /**
     * Creates a new username for the player using the client connected to the clientHandler.
     *
     * @param username    is the username of the player using the client connected to the clientHandler;
     * @param clientInput is the command input line as received from the client.
     */
    public void createUsername(String username, String clientInput) {
        if ((server.getListOfUsernames().contains(username))) {
            sendUsernameTaken("This username is already used by another player; choose another username.");
        } else {
            System.out.println(clientInput);
            server.addUsername(username);
            sendJoined(username + " has successfully connected to the server.");
            saveUsername(username);
        }
    }

    /**
     * Saves the username to be able to reuse it in other commends.
     *
     * @param username is the username the client thread is using.
     */
    public void saveUsername(String username) {
        usernameStored = username;
    }

    /**
     * Gets the username of the client thread.
     *
     * @return the username of the client thread.
     */
    public String getUsername() {
        return usernameStored;
    }

    /**
     * Puts the player using the client connected to this clientHandler in the queue.
     *
     * @param clientInput is the command input line as received from the client.
     */
    public void enterQueue(String clientInput) {
        server.addToQueue(this);
        System.out.println(clientInput);
    }

    /**
     * Removes the player using the client connected to this clientHandler from the queue.
     */
    public void leaveQueue() {
        sendJoined(getUsername() + " has left the queue.");
        server.removeFromQueue(this);
    }
}

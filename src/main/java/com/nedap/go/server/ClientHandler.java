package com.nedap.go.server;

import com.nedap.go.Protocol;
import com.nedap.go.game.Board;
import com.nedap.go.game.Game;
import com.nedap.go.game.Player;
import com.nedap.go.game.Stone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a clientHandler of the server for a connected client.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter printWriter;
    private BufferedReader input;
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
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread clientHandlerThread = new Thread(this);
            clientHandlerThread.start();
        } catch (IOException e) {
            System.out.println("Connection with the server could not be established.");
        }
    }

    /**
     * Closes the clientHandler. To do so, first the client socket is closed, and both the printWriter and
     * bufferedReader are closed too to prevent leakage. Besides, the server removes this clientHandler from the
     * list of connected clients, it removes the username from the list of usernames and if the client connected to
     * this clientHandler was in the queue for playing the game, it is removed from the queue as well.
     */
    public void close() {
        try {
            socket.close();
            printWriter.close();
            input.close();
            server.removeClient(this);
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
                String line = input.readLine();
                if (line == null) {
                    close();
                    break;
                }
                String[] split = line.split(SEPARATOR);
                String command = split[0];
                switch (command) {
                    case HELLO:
                        System.out.println(line);
                        sendWelcome("Server by Arjonne");
                        break;
                    case USERNAME:
                        String username = split[1];
                        createUsername(username, line);
                        break;
                    case QUEUE:
                        queueCount++;
                        if (queueCount % 2 != 0) {
                            enterQueue(line);
                        } else {
                            leaveQueue();
                        }
                        break;
                    case MOVE:
                        sendMove(getUsername(), 1, 3);
//                        doMove(getUsername(), row, column);
//                        game.doMove(row, column);
                        break;
                    case PASS:
                        sendPass(getUsername());
//                        doPass(username);
//                        game.pass();
                        break;
                    case QUIT:
                        this.close();
                        break;
                    default:
                        sendError("The input is not correct.");
                        break;
                }
            } catch (IOException e) {
                close();
                break;
            }
        }

    }

    /**
     * Sends the welcome message as part of the handshake in the correct format to the client. Besides that,
     * the formatted message is visible in the server TUI.
     *
     * @param serverID is the ID of the server
     */
    public void sendWelcome(String serverID) {
        String serverIdFormatted = Protocol.welcomeMessage(serverID);
        printWriter.println(serverIdFormatted);
        System.out.println(serverIdFormatted);
    }

    /**
     * Sends a message that the entered username is already taken as part of the handshake in the correct format to the
     * clientHandler. Besides that, the formatted message is visible in the server TUI.
     *
     * @param message is the message that describes that the username is not accepted as it is already in use
     */
    public void sendUsernameTaken(String message) {
        String messageFormatted = Protocol.usernameTaken(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Sends a message that the client is correctly connected to the server as part of the handshake in the correct
     * format to the clientHandler. Besides that, the formatted message is visible in the server TUI.
     *
     * @param message is the message with description of the correct connection for the client
     */
    public void sendJoined(String message) {
        String messageFormatted = Protocol.joined(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Sends a message to the client that a new game is started in the correct format to the clientHandler. Besides
     * that, the formatted message is visible in the server TUI.
     */
    public void sendNewGame() {
        //TODO all logic in serverThread that represents the game.
        if (server.getWaitingQueue().size() < 2) {
            System.out.println(getUsername() + " has successfully entered the queue. Waiting for a second player....");
        } else {
            createNewGame();
        }
    }

    /**
     * Sends a message to the client whose turn it is in the correct format to the clientHandler. Besides
     * that, the formatted message is visible in the server TUI.
     */
    public void sendYourTurn() {
        String yourTurn = Protocol.yourTurn();
        printWriter.println(yourTurn);
        System.out.println(yourTurn);
    }

    /**
     * Sends a message to both clients with which client made what move in the correct format to the clientHandler.
     * Besides that, the formatted message is visible in the server TUI.
     *
     * @param username is the username of the client that made this move
     * @param row      is the row of the position of this move
     * @param column   is the column of the position of this move
     */
    public void sendMove(String username, int row, int column) {
        String moveFormatted = Protocol.move(username, row, column);
//        for (ClientHandler handlers : clientHandlerList) {
//            handlers.printWriter.println(messageFormatted);
//        }
        System.out.println(moveFormatted);
    }

    /**
     * Sends a message to both clients with which client passed in the correct format to the clientHandler. Besides
     * that, the formatted message is visible in the server TUI.
     *
     * @param username is the username of the client that passed
     */
    public void sendPass(String username) {
        String passFormatted = Protocol.pass(username);
//        for (ClientHandler handlers : clientHandlerList) {
//            handlers.printWriter.println(messageFormatted);
//        }
        System.out.println(passFormatted);
    }

    /**
     * Sends a message to the client that an invalid move was made in the correct format to the clientHandler. This
     * message was only sent to the client that made this invalid move. Besides that, the formatted message is visible
     * in the server TUI.
     */
    public void sendInvalidMove() {
        String invalidMoveFormatted = Protocol.invalidMove();
        printWriter.println(invalidMoveFormatted);
        System.out.println(invalidMoveFormatted);
    }

    /**
     * Sends a message to both clients that the game is over in the correct format to the clientHandler. This message is
     * sent to both participating clients. Besides that, the formatted message is visible in the server TUI.
     */
    public void sendGameOver(String reason, String usernameWinner) {
        String gameOverFormatted = Protocol.gameOver(reason, usernameWinner);
//        for (ClientHandler handlers : clientHandlerList) {
//            handlers.printWriter.println(messageFormatted);
//        }
        System.out.println(gameOverFormatted);
    }

    /**
     * Sends the error command in the correct format to the client that caused this error. Besides that, the formatted
     * message is visible in the server TUI.
     *
     * @param message is the error message that explains the error
     */
    public void sendError(String message) {
        String messageFormatted = Protocol.error(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    /**
     * Creates a new username for the player using the client connected to the clientHandler.
     *
     * @param username is the username of the player using the client connected to the clientHandler
     * @param line is the command input line
     */
    public void createUsername(String username, String line) {
        if ((server.getListOfUsernames().contains(username))) {
            sendUsernameTaken("This username is already used by another player; choose another username.");
        } else {
            System.out.println(line);
            server.addUsername(username);
            sendJoined(username + " has successfully connected to the server.");
            saveUsername(username);
        }
    }

    /**
     * Saves the username to be able to reuse it in other commends.
     *
     * @param username is the username the client thread is using
     */
    public void saveUsername(String username) {
        usernameStored = username;
    }

    /**
     * Gets the username of the client thread.
     *
     * @return the username of the client thread
     */
    public String getUsername() {
        return usernameStored;
    }

    /**
     * Puts the player using the client connected to this clientHandler in the queue.
     *
     * @param line is the command input line
     */
    public void enterQueue(String line) {
        server.addToQueue(this);
        sendNewGame();
        System.out.println(line);
    }

    /**
     * Removes the player using the client connected to this clientHandler from the queue.
     */
    public void leaveQueue() {
        sendJoined(getUsername() + " has left the queue.");
        server.removeFromQueue(this);
    }

    /**
     * Creates a new thread for playing a game with two players. Here, the game is created as well.
     */
    public void createNewGame() {
        // first, create a new list in which the clientHandlers of the two players can be stored.
        List<ClientHandler> clientHandlerList = new ArrayList<>();
        // then, add the clientHandlers from the two clients that are in front of the queue.
        clientHandlerList.add(server.getWaitingQueue().poll());
        clientHandlerList.add(server.getWaitingQueue().poll());
        ClientHandler clientHandler1 = clientHandlerList.get(0);
        ClientHandler clientHandler2 = clientHandlerList.get(1);
        // get the username of the two clients connected to the clientHandlers to be able to create a new game.
        String username1 = clientHandler1.getUsername();
        String username2 = clientHandler2.getUsername();
        // send the message that a new game is started to both clients connected to the clientHandlers.
        String messageFormatted = Protocol.newGame(username1, username2);
        for (ClientHandler handlers : clientHandlerList) {
            handlers.printWriter.println(messageFormatted);
        }
        // put this message in the server log as well.
        System.out.println(messageFormatted);
        // create the new game.
        Player playerBlack = new Player(username1, Stone.BLACK);
        Player playerWhite = new Player(username2, Stone.WHITE);
        Board board = new Board();
        Game game = new Game(playerBlack, playerWhite, board);
        // as player Black always starts the game, this player gets the YOURTURN message:
        clientHandler1.sendYourTurn();
    }
}


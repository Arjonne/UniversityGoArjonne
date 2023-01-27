package com.nedap.go.server;

import com.nedap.go.Protocol;
import com.nedap.go.client.Client;
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
 * Represents a clientHandler for the server for the GO game.
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
     * list of connected clients.
     */
    public void close() {
        try {
            socket.close();
            printWriter.close();
            input.close();
            server.removeClient(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs this operation.
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
                        doWelcome("Server by Arjonne");
                        break;
                    case USERNAME:
                        String username = split[1];
                        if ((server.getListOfUsernames().contains(username))) {
                            doUsernameTaken("This username is already used by another player; choose another username.");
                        } else {
                            System.out.println(line);
                            server.addUsername(username);
                            doJoined(username + " has successfully connected to the server.");
                            saveUsername(username);
                        }
                        break;
                    case QUEUE:
                        queueCount++;
                        if (queueCount % 2 != 0) {
                            server.addToQueue(this);
                            doNewGame();
                        } else {
                            doJoined(getUsername() + " has left the queue.");
                            server.removeFromQueue(this);
                        }
                        break;
                    case MOVE:
//                    doActivate(param);
                        break;
                    case PASS:
//                    doHelp();
                        break;
                    case QUIT:
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

    public void doError(String message) {
        String messageFormatted = Protocol.error(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    public void doWelcome(String serverID) {
        String serverIdFormatted = Protocol.welcomeMessage(serverID);
        printWriter.println(serverIdFormatted);
        System.out.println(serverIdFormatted);
    }

    public void doUsernameTaken(String message) {
        String messageFormatted = Protocol.usernameTaken(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    public void doJoined(String message) {
        String messageFormatted = Protocol.joined(message);
        printWriter.println(messageFormatted);
        System.out.println(messageFormatted);
    }

    public void saveUsername(String username) {
        usernameStored = username;
    }

    public String getUsername() {
        return usernameStored;
    }

    public void doNewGame() {
        if (server.getWaitingQueue().size() < 2) {
            System.out.println(getUsername() + " has successfully entered the queue. Waiting for a second player....");
        } else {
            List<ClientHandler> clientHandlerList = new ArrayList<>();
            clientHandlerList.add(server.getWaitingQueue().poll());
            clientHandlerList.add(server.getWaitingQueue().poll());
            String username1 = clientHandlerList.get(0).getUsername();
            String username2 = clientHandlerList.get(1).getUsername();
            String messageFormatted = Protocol.newGame(username1, username2);
            System.out.println(messageFormatted);
            for (ClientHandler handlers : clientHandlerList) {
                handlers.printWriter.println(messageFormatted);
            }
            Player playerBlack = new Player(username1, Stone.BLACK);
            Player playerWhite = new Player(username2, Stone.WHITE);
            Board board = new Board();
            Game game = new Game(playerBlack, playerWhite, board);
        }
    }
}


package com.nedap.go.client;

import com.nedap.go.Protocol;
import com.nedap.go.game.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Represents the client for the GO game.
 */
public class Client implements Runnable {
    private final ClientTUI clientTui;
    //TODO check what needs to be global and what is a local variable!
    private Socket socket;
    private BufferedReader inputFromClientHandler;
    private PrintWriter writerToClientHandler;
    private String username;
    private Stone stone;
    private Game goGame;

    public static final String SEPARATOR = "~";
    public static final String WELCOME = "WELCOME";
    public static final String USERNAMETAKEN = "USERNAMETAKEN";
    public static final String JOINED = "JOINED";
    public static final String NEWGAME = "NEWGAME";
    public static final String GAMEOVER = "GAMEOVER";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String VICTORY = "VICTORY";
    public static final String YOURTURN = "YOURTURN";
    public static final String INVALIDMOVE = "INVALIDMOVE";
    public static final String MOVE = "MOVE";

    // Methods needed to create, run and close this client:

    /**
     * Creates a new client via which a player can play the game.
     */
    public Client(ClientTUI clientTUI) {
        this.clientTui = clientTUI;
    }

    /**
     * Connects the client to the server.
     *
     * @param address is the network address of the server;
     * @param port    is the port number on which the server is accepting a connection for the GO game;
     * @return true if connection could be established, false if not.
     */
    public boolean connect(InetAddress address, int port) {
        try {
            socket = new Socket(address, port);
            inputFromClientHandler = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writerToClientHandler = new PrintWriter(socket.getOutputStream(), true);
            Thread clientThread = new Thread(this);
            clientThread.start();
        } catch (IOException e) {
            System.out.println("Connection with the server could not be established.");
            return false;
        }
        return true;
    }

    /**
     * Closes the connection between client and server by closing the client socket. Besides that, the bufferedReader
     * and printWriter should be closed too to prevent data leakage.
     */
    public void close() {
        try {
            socket.close();
            inputFromClientHandler.close();
            writerToClientHandler.close();
        } catch (IOException e) {
            System.out.println("Not able to close this client");
        }
    }

    /**
     * Runs this operation. As long as the socket is not closed (= as long as the client is connected to the server),
     * the input from the server (via the clientHandler) can be processed. Input comes in from and goes back to the
     * clientHandler via predefined format as described in the protocol. If input from the console is needed, the
     * connected boolean in the clientTUI will be changed to be able to invoke methods in there.
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String input = inputFromClientHandler.readLine();
                if (input == null) {
                    close(); // ? --> deze close gaat niet goed!! Message meesturen dat'ie geclosed is zodat client
                    // handmatig kan closen?
                    break;
                }
                String[] split = input.split(SEPARATOR);
                String command = split[0];
                switch (command) {
                    case WELCOME:
                        clientTui.setUsernameCanBeCreated(true);
                        break;
                    case USERNAMETAKEN:
                        System.out.println(split[1]);
                        clientTui.setUsernameIsTaken(true);
                        break;
                    case JOINED:
                        clientTui.setWantsToEnterQueue(true);
                        break;
                    case NEWGAME:
                        clientTui.setWantsToEnterQueue(false);
                        String username1 = split[1];
                        String username2 = split[2];
                        startNewGame(username1, username2);
                        break;
                    case YOURTURN:
                        System.out.println("It is your turn!");
                        clientTui.setWantsToDetermineMove(true);
                        break;
                    case MOVE:
                        if (split[2].equals("PASS")) {
                            System.out.println(split[1] + " passed.");
                            goGame.pass();
                        } else {
                            System.out.println(split[1] + " placed a stone on row " + split[2] + " and column " + split[3] +".");
                            int row = Integer.parseInt(split[2]);
                            int column = Integer.parseInt(split[3]);
                            goGame.doMove(row, column);
                        }
                        break;
                    case INVALIDMOVE:
                        System.out.println("Not a valid move, try again:");
                        clientTui.setWantsToDetermineMove(true);
                        break;
                    case GAMEOVER:
                        if (split[1].equals(VICTORY)) {
                            System.out.println("The game is over due to two consecutive passes or because the board is full. The winner is: " + split[2] + ".");
                        } else if (split[1].equals(DISCONNECT)) {
                            System.out.println("The game is over because of disconnection. The winner is " + split[2] + ".");
                        }
                        clientTui.setWantsToPlayNewGame(true);
                        break;
                    default:
                        System.out.println("No valid input");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Socket is closed.");

            }
        }
    }

    // Methods needed to send the messages to the server via the clientHandler:

    /**
     * Sends the hello message as part of the handshake in the correct format to the clientHandler.
     *
     * @param clientID is the ID of the client
     */
    public synchronized void sendHello(String clientID) {
        String clientIdFormatted = Protocol.helloMessage(clientID);
        writerToClientHandler.println(clientIdFormatted);
    }

    /**
     * Sends the username message as part of the handshake in the correct format to the clientHandler.
     *
     * @param username is the username the client want to use
     */
    public synchronized void sendUsername(String username) {
        String usernameFormatted = Protocol.username(username);
        writerToClientHandler.println(usernameFormatted);
    }

    /**
     * Sends the queue command in the correct format to the clientHandler.
     */
    public synchronized void sendQueue() {
        String queueFormatted = Protocol.queue();
        writerToClientHandler.println(queueFormatted);
    }

    /**
     * Sends the move command in the correct format to the clientHandler.
     *
     * @param row    is the row of the position of the stone to place
     * @param column is the column of the position of the stone to place
     */
    public synchronized void sendMove(int row, int column) {
        String moveFormatted = Protocol.move(row, column);
        writerToClientHandler.println(moveFormatted);
    }

    /**
     * Sends the pass command in the correct format to the clientHandler.
     */
    public synchronized void sendPass() {
        String passFormatted = Protocol.pass();
        writerToClientHandler.println(passFormatted);
    }

    /**
     * Sends the quit command in the correct format to the clientHandler.
     */
    public synchronized void sendQuit() {
        String quitFormatted = Protocol.quit();
        writerToClientHandler.println(quitFormatted);
    }

    // Methods needed to process information received from the server via the clientHandler:

    /**
     * Sets the username to be able to reuse this in other commands.
     *
     * @param username is the username of the player using this client
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username of the player using this client.
     *
     * @return the username of the player using this client
     */
    public String getUsername() {
        return username;
    }

    /**
     * Starts a new game.
     *
     * @param username1 username of player 1
     * @param username2 username of player 2
     */
    public void startNewGame(String username1, String username2) {
        // creates a humanPlayer or computerPlayer on the scanner input thread.
        clientTui.setWantsToCreatePlayerType(true);
        while (clientTui.getWantsToCreatePlayerType()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Not able to sleep for 2 seconds due to interruption.");
            }
            clientTui.setWantsToDetermineMove(false);
        }
        System.out.println("A new game is started. " + username1 + " is playing against " + username2 + ".");
        System.out.println(username1 + " is BLACK, " + username2 + " is WHITE.");
        // create new player with stone input based on username and being player 1 (black) or player 2 (white).
        if (getUsername().equals(username1)) {
            stone = Stone.BLACK;
        } else {
            stone = Stone.WHITE;
        }
        // create a new game to be able to have a board and keep track of all moves on the client side.
        goGame = new Game(new Player(username1, Stone.BLACK), new Player(username2, Stone.WHITE), new Board(), new GoGUI(Board.SIZE));
    }

    /**
     * Gets the stone type assigned to the player using this client.
     *
     * @return the stone, which is either black or white
     */
    public Stone getStone() {
        return stone;
    }

    /**
     * Get the game in the current state.
     *
     * @return the game in its current state
     */
    public Game getGoGame() {
        return goGame;
    }
}






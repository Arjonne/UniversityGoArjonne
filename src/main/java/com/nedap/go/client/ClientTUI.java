package com.nedap.go.client;

import com.nedap.go.game.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the textual user interface for starting a new client and establish the connection with the server.
 */
public class ClientTUI {
    private static final String SEPARATOR = "~";
    private Scanner scanner;
    private Client client;
    private Player playerType;
    private Player humanPlayer;
    private ComputerPlayer computerPlayer;
    private String input;
    private Position nextMove;
    private boolean usernameCanBeCreated = false;
    private boolean usernameIsTaken = false;
    private boolean wantsToEnterQueue = false;
    private boolean wantsToLeaveQueue = false;
    private boolean wantsToCreatePlayerType = false;
    private boolean wantsToDetermineMove = false;
    private boolean wantsToPlayNewGame = false;
    private boolean quit;

    public ClientTUI() {
    }

    /**
     *
     */
    public void go() {
        // let your program ask for a server address, a server port and a username.
        System.out.println("Import a server address, a server port number and a username.");
        // enter the server address and the port that are the output of the ServerTUI to connect the server and client:
        System.out.println("First, enter the server address: ");
        scanner = new Scanner(System.in);
        try {
            InetAddress address = InetAddress.getByName(scanner.nextLine());
            System.out.println("Now, enter the port number to connect to the server: ");
            try {
                int port = scanner.nextInt();
                scanner.nextLine();
                client = new Client(this);
                // try to connect the client to the server. If connection is established, the handshake will
                // automatically start. Then, all input from the server that is received via the clientHandler will
                // be processed by the run() methods of the client thread:
                if (client.connect(address, port)) {
                    client.sendHello("Client by Arjonne");
                }
                quit = false;
                while (!(quit || System.in.available()>0)) {
                    if (usernameCanBeCreated || usernameIsTaken) {
                        createUsername();
                    } else if (wantsToEnterQueue) {
                        enterQueue();
//                    } else if (wantsToLeaveQueue) {
//                        leaveQueue();
                    } else if (wantsToCreatePlayerType) {
                        createPlayerType(client.getUsername(), client.getStone());
                    } else if (wantsToDetermineMove) {
                        determineNextMove();
                    } else if (wantsToPlayNewGame) {
                        newGame();
                    }
                }
                // handle exception when port number input was not a number and try again from the beginning:
            } catch (InputMismatchException e) {
                System.out.println("No valid input: port number should be between 1 and 65535; use 0 for a random available port.");
            } catch (IOException e) {
                System.out.println("Not possible.");
            }
            // handle exception when connection could not be established as address or port were not correct and try
            // again from the beginning:
        } catch (UnknownHostException e) {
            System.out.println("The IP address for the host could not be found so connection is not established.");
        }
    }

    /**
     * Changes the boolean to be able to know whether the username should be created now.
     *
     * @param usernameCanBeCreated is the boolean that represents whether a username can be created or not.
     */
    public void setUsernameCanBeCreated(boolean usernameCanBeCreated) {
        this.usernameCanBeCreated = usernameCanBeCreated;
    }

    /**
     * Changes the boolean to be able to know whether the username is already taken and should be created again.
     *
     * @param usernameIsTaken is the boolean that represents whether a username must be recreated or not.
     */
    public void setUsernameIsTaken(boolean usernameIsTaken) {
        this.usernameIsTaken = usernameIsTaken;
    }

    /**
     * Changes the boolean to be able to know whether the player using this client wants to enter the queue for playing a GO game.
     *
     * @param wantsToEnterQueue is the boolean that represents whether the player using this client wants to enter the queue.
     */
    public void setWantsToEnterQueue(boolean wantsToEnterQueue) {
        this.wantsToEnterQueue = wantsToEnterQueue;
    }

    /**
     * Changes the boolean to be able to know whether the player using this client wants to leave the queue for playing a GO game.
     *
     * @param wantsToLeaveQueue is the boolean that represents whether the player using this client wants to leave the queue.
     */
    public void setWantsToLeaveQueue(boolean wantsToLeaveQueue) {
        this.wantsToLeaveQueue = wantsToLeaveQueue;
    }

    /**
     * Changes the boolean to be able to know whether the player using this client wants to create a player type for playing a GO game.
     *
     * @param wantsToCreatePlayerType is the boolean that represents whether the player using this client wants to create a player type.
     */
    public void setWantsToCreatePlayerType(boolean wantsToCreatePlayerType) {
        this.wantsToCreatePlayerType = wantsToCreatePlayerType;
    }

    /**
     * Gets the boolean to be able to know whether the boolean for creating the player type is already set to true.
     */
    public boolean getWantsToCreatePlayerType() {
        return wantsToCreatePlayerType;
    }

    /**
     * Changes the boolean to be able to know whether the player using this client wants to determine the move for this GO game.
     *
     * @param wantsToDetermineMove is the boolean that represents whether the player using this client wants to determine a move.
     */
    public void setWantsToDetermineMove(boolean wantsToDetermineMove) {
        this.wantsToDetermineMove = wantsToDetermineMove;
    }

    /**
     * Changes the boolean to be able to know whether the player using this client wants to play a new game after one game is finished.
     *
     * @param wantsToPlayNewGame is the boolean that represents whether the player using this client wants to play a new game.
     */
    public void setWantsToPlayNewGame(boolean wantsToPlayNewGame) {
        this.wantsToPlayNewGame = wantsToPlayNewGame;
    }

    /**
     * Checks whether the input from the console is equal to quit.
     */
    public boolean checkForQuitInput() {
        if (input.equals("quit") || input.equals("QUIT")) {
            quit = true;
            client.close();
            client.sendQuit();
            System.out.println("Disconnected");
            return true;
        }
        return false;
    }

    /**
     * Creates a new username for the player using this client.
     */
    public void createUsername() {
        System.out.println("Enter your username:");
        input = scanner.nextLine();
        if (checkForQuitInput()) {
            return;
        }
        while (input.contains(SEPARATOR)) {
            System.out.println("Invalid username: it may not contain a ~. Try again:");
            input = scanner.nextLine();
            if (checkForQuitInput()) {
                return;
            }
        }
        client.setUsername(input);
        client.sendUsername(input);
        usernameCanBeCreated = false;
        usernameIsTaken = false;
    }

    /**
     * Player using this client enters the queue to wait for a second player.
     */
    public void enterQueue() {
        System.out.println("Do you want to enter the queue and wait for a second player to play GO? Answer with YES or NO:");
        input = scanner.nextLine().toUpperCase();
        if (checkForQuitInput()) {
            return;
        }
        while (!(input.equals("YES") || input.equals("NO"))) {
            System.out.println("Unable to understand your input. Try again:");
            input = scanner.nextLine().toUpperCase();
            if (checkForQuitInput()) {
                return;
            }
        }
        if (input.equals("YES")) {
            System.out.println("You have successfully entered the queue. Waiting for a second player....");
            client.sendQueue();
            wantsToLeaveQueue = true;
        } else {
            System.out.println("The connection will be broken.");
            client.sendQuit();
            wantsToLeaveQueue = false;
            quit = true;
        }
        wantsToEnterQueue = false;
    }

//    /**
//     * Player using this client leaves the queue and stops waiting for a second player.
//     */
//    public void leaveQueue() {
//        System.out.println("Do you want to leave the queue again? If you want to leave, answer with YES:");
//        input = scanner.nextLine().toUpperCase();
//        if (checkForQuitInput()) {
//            return;
//        }
//        if (!input.equals("YES")) {
//            System.out.println("Unable to understand your input. Try again:");
//            input = scanner.nextLine().toUpperCase();
//            if (checkForQuitInput()) {
//                return;
//            }
//        }
//        if (input.equals("YES")) {
//            System.out.println("You have successfully left the queue.");
//            client.sendQueue();
//            wantsToEnterQueue = true;
//            wantsToLeaveQueue = false;
//        }
//    }

    /**
     * Creates a new player type, based on the input of the player using this client.
     *
     * @param username is the username of the player using this client
     */
    public void createPlayerType(String username, Stone stone) {
        System.out.println("Do you want to play a game with own input or with input defined by the computer? For own input, type OWN, for computer input, type COMPUTER:");
        input = scanner.nextLine().toUpperCase();
        if (checkForQuitInput()) {
            return;
        }
        while (!(input.equals("OWN") || input.equals("COMPUTER"))) {
            System.out.println("Unable to understand your input. Try again:");
            input = scanner.nextLine().toUpperCase();
            if (checkForQuitInput()) {
                return;
            }
        }
        if (input.equals("OWN")) {
            humanPlayer = new Player(username, stone);
            setPlayerType(humanPlayer);
            System.out.println("Human player created. Wait for your turn.");
        } else {
            computerPlayer = new ComputerPlayer(username, stone);
            setPlayerType(computerPlayer);
            System.out.println("Computer player created. Wait for your turn.");
        }
        wantsToCreatePlayerType = false;
    }

    /**
     * Sets the player type after creating the new player.
     *
     * @return the player type (human or computer)
     */
    public Player getPlayerType() {
        return playerType;
    }

    /**
     * Gets the player type after creating the new player.
     *
     * @param playerType is the player type the player using this client wants to use
     */
    public void setPlayerType(Player playerType) {
        this.playerType = playerType;
    }


    /**
     * Player using this client can start a new game.
     */
    public void newGame() {
        System.out.println("Do you want to play a new game? Answer with YES or NO:");
        input = scanner.nextLine().toUpperCase();
        if (checkForQuitInput()) {
            return;
        }
        while (!(input.equals("YES") || input.equals("NO"))) {
            System.out.println("Unable to understand your input. Try again:");
            input = scanner.nextLine().toUpperCase();
        }
        if (input.equals("YES")) {
            client.sendQueue();
        } else {
            System.out.println("The connection will be broken.");
            client.sendQuit();
            quit = true;
        }
        wantsToPlayNewGame = false;
    }

    /**
     * Determines the next move the player using this client wants to make.
     */
    public void determineNextMove() {
        if (getPlayerType() == humanPlayer) {
            nextMove = determineMoveHumanPlayer();
        } else {
            nextMove = determineMoveComputerPlayer(client.getGoGame());
        }
        if (nextMove == null) {
            client.sendPass();

        } else {
            client.sendMove(nextMove.getRow(), nextMove.getColumn());
        }
        wantsToDetermineMove = false;
    }

    public Position determineMoveHumanPlayer() {
        System.out.println("Do you want to make a move or pass? Type either MOVE or PASS:");
        input = scanner.nextLine().toUpperCase();
        checkForQuitInput();
        int row = -1;
        int column = -1;
        if (input.equals("PASS")) {
            return null;
        } else if (input.equals("MOVE")) {
            boolean correctInput = false;
            while (!correctInput) {
                System.out.print("On what intersection do you want to place your stone? First, enter the row number (ranging between 1 and " + Board.SIZE + "): ");
                try {
                    row = (scanner.nextInt() - 1);
                System.out.print("Now, enter the column number (ranging between 1 and " + (Board.SIZE) + "): ");
                    column = (scanner.nextInt() - 1);
                    correctInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Input must be a number between 1 and " + Board.SIZE + "!");
                }
            }
        }
        return new Position(row, column);
    }

    public Position determineMoveComputerPlayer(Game game) {
        return game.findRandomValidPosition();
    }

    public static void main(String[] args) {
        ClientTUI clientTUI = new ClientTUI();
        clientTUI.go();
    }
}



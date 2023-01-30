package com.nedap.go.client;

import com.nedap.go.game.ComputerPlayer;
import com.nedap.go.game.Player;
import com.nedap.go.game.Position;
import com.nedap.go.game.Stone;

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
    private boolean hasReceivedWelcome = false;


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
                // automatically start. Then, all input will be processed by the run() methods of the client thread:
                if (client.connect(address, port)) {
                    client.sendHello("Client by Arjonne");
                    client.run();
                }
                boolean quit = false;
                while (!quit) {
                    String input = scanner.nextLine();
                    if (input.equals("quit")) {
                        quit = true;
                        client.close();
                        client.sendQuit();
                    }
//                    if (isHasReceivedWelcome()) {
//                        String username = createUsername();
//                        client.setUsername(username);
//                        client.setUsernameCreated(true);
//                    }


                }
                // handle exception when port number input was not a number and try again from the beginning:
            } catch (InputMismatchException e) {
                System.out.println("No valid input: port number should be between 1 and 65535; use 0 for a random available port.");
            }
            // handle exception when connection could not be established as address or port were not correct and try
            // again from the beginning:
        } catch (UnknownHostException e) {
            System.out.println("The IP address for the host could not be found so connection is not established.");
        }
    }

    public boolean isHasReceivedWelcome() {
        return hasReceivedWelcome;
    }

    public void setHasReceivedWelcome(boolean hasReceivedWelcome) {
        this.hasReceivedWelcome = hasReceivedWelcome;
    }

    /**
     * Creates a new username for the player using this client.
     */
    public String createUsername() {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        while (username.contains(SEPARATOR)) {
            System.out.println("Invalid username: it may not contain a ~. Try again:");
            username = scanner.nextLine();
        }
        return username;
    }

    /**
     * Player using this client enters the queue to wait for a second player.
     */
    public boolean enterQueue() {
        System.out.println("Do you want to enter the queue and wait for a second player to play GO? Answer with YES or NO:");
        String answer = scanner.nextLine().toUpperCase();
        while (!(answer.equals("YES") || answer.equals("NO"))) {
            System.out.println("Unable to understand your input. Try again:");
            answer = scanner.nextLine().toUpperCase();
        }
        if (answer.equals("YES")) {
            System.out.println("You have successfully entered the queue. Waiting for a second player....");
            return true;
        } else {
            System.out.println("The connection will be broken.");
            return false;
        }
    }

    /**
     * Player using this client leaves the queue and stops waiting for a second player.
     */
    public boolean leaveQueue() {
        while (client.getCommand().equals("JOINED")) {
            System.out.println("Do you want to leave the queue again? Answer with YES or NO:");
            String answer = scanner.nextLine().toUpperCase();
            if (!(answer.equals("YES") || answer.equals("NO"))) {
                System.out.println("Unable to understand your input. Try again:");
                answer = scanner.nextLine().toUpperCase();
            }
            if (answer.equals("YES")) {
                System.out.println("You have successfully left the queue.");
                return true;
            } else if (answer.equals("NO")) {
                System.out.println("Still waiting for a second player....");
                return false;
            }
        }
        return false;
    }

    /**
     * Creates a new player type, based on the input of the player using this client.
     *
     * @param username is the username of the player using this client
     */
    public Player createPlayerType(String username, Stone stone) {
        System.out.println("Do you want to play a game with own input or with input defined by the computer? For own input, type OWN, for computer input, type COMPUTER:");
        String playerType = scanner.nextLine().toUpperCase();
        while (!(playerType.equals("OWN") || playerType.equals("COMPUTER"))) {
            System.out.println("Unable to understand your input. Try again:");
            playerType = scanner.nextLine().toUpperCase();
        }
        if (playerType.equals("OWN")) {
            Player humanPlayer = new Player(username, stone);
            setPlayerType(humanPlayer);
            return humanPlayer;
        } else {
            ComputerPlayer computerPlayer = new ComputerPlayer(username, stone);
            setPlayerType(computerPlayer);
            return computerPlayer;
        }
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
     * Determines the next move the player using this client wants to make.
     *
     * @return the position of the next move. Can also be null (then pass)
     */
    public Position determineNextMove() {
        return getPlayerType().determineMove(goGame);
    }

    /**
     * Player using this client can start a new game.
     *
     * @return true if player using this client wants to start a new game, false if not
     */
    public boolean newGame() {
        System.out.println("Do you want to play a new game? Answer with YES or NO:");
        String answer = scanner.nextLine().toUpperCase();
        while (!(answer.equals("YES") || answer.equals("NO"))) {
            System.out.println("Unable to understand your input. Try again:");
            answer = scanner.nextLine().toUpperCase();
        }
        if (answer.equals("YES")) {
            System.out.println("You have successfully entered the queue. Waiting for a second player....");
            return true;
        } else {
            System.out.println("The connection will be broken.");
            return false;
        }
    }


    public static void main(String[] args) {
        ClientTUI clientTUI = new ClientTUI();
        clientTUI.go();
    }
}



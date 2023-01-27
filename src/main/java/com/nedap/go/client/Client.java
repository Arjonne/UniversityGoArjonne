package com.nedap.go.client;

import com.nedap.go.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Represents the client for the GO game.
 */
public class Client implements Runnable {
    //TODO check what needs to be global and what is a local variable!
    private Socket socket;
    private BufferedReader input;
    private PrintWriter writer;

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
    public static final String MOVE = "MOVE"; // both in server and client side?  username played this move || username passed
    public static final String ERROR = "ERROR";

    /**
     * Creates a new client via which a player can play the game.
     */
    public Client() {
    }

    /**
     * Connects the client to the server.
     *
     * @param address is the network address of the server
     * @param port    is the port number on which the server is accepting a connection for the GO game
     * @return true if connection could be established, false if not
     */
    public boolean connect(InetAddress address, int port) {
        try {
            socket = new Socket(address, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
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
            input.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("Not able to close this client");
        }
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String line = input.readLine();
                if (line == null) {
                    close(); // ? --> deze close gaat niet goed!! Want ook als ~ ingevoerd wordt dan closet 'ie niet goed. Message meesturen
                    // dat'ie geclosed is zodat client handmatig kan closen?
                    break;
                }
                Scanner scanner = new Scanner(System.in);
                String[] split = line.split(SEPARATOR);
                String command = split[0];
                switch (command) {
                    case WELCOME:
                        System.out.println("Enter your username:");
                        String username = scanner.nextLine();
                        if (username.contains(SEPARATOR)) {
                            System.out.println("Invalid username: it may not contain a ~.");
                            doHello(this.toString());
                        } else {
                            doUsername(username);
                        }
                        break;
                    case USERNAMETAKEN:
                        System.out.println(split[1]);
                        doHello(this.toString());
                        break;
                    case JOINED:
                        System.out.println(split[1]);
                        System.out.println("Do you want to enter the queue and wait for a second player to play GO? Type YES for entering the queue.");
                        if (scanner.nextLine().toUpperCase().equals("YES")) {
                            doQueue();
                            System.out.println("You have successfully entered the queue. Waiting for a second player....");
                            System.out.println("If you want to leave the queue, type YES again.");
                            if (scanner.nextLine().toUpperCase().equals("YES")) {
                                doQueue();
                            }
                        }
                        break;
                    case NEWGAME:
                        System.out.println("A new game is started. " + split[1] + " is playing against " + split[2] + ".");
                        System.out.println(split[1] + " is BLACK, " + split[2] + " is WHITE.");
                        break;
                    case YOURTURN:
                        break;
                    case GAMEOVER:
                        break;
                    case ERROR:
                        break;
                }


                //doHello(this.toString());


                // this is how client commands are need to be handled!
//                String[] messagesString = line.split("~", 3);
//                for (Listener listener : listeners) {
//                    listener.messageReceived(messagesString[1], messagesString[2]);
//                }
            } catch (IOException e) {
                System.out.println("Socket is closed.");

            }
        }
    }

    public synchronized void doHello(String clientID) {
        String clientIdFormatted = Protocol.helloMessage(clientID);
        writer.println(clientIdFormatted);
    }

    public synchronized void doUsername(String username) {
        String usernameFormatted = Protocol.username(username);
        writer.println(usernameFormatted);
    }

    public synchronized void doQueue() {
        String queueing = Protocol.queue();
        writer.println(queueing);
    }
}






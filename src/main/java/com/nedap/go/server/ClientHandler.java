package com.nedap.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents a clientHandler for the server for the GO game.
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter printWriter;
    private BufferedReader input;
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
     * Gets the string representation of the clientHandler.
     *
     * @return the clientHandler to string
     */
    public String getClientHandler() {
        return this.toString();
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
                    close();
                    break;
                }
                boolean quit = false;
                while (!quit) {
                    String[] split = line.split(SEPARATOR);
                    String command = split[0];
                    switch (command) {
                        case HELLO:
                          //  System.out.println("a");
                            doWelcome();
                            break;
                        case USERNAME:
//                    doOut(param);
                            break;
                        case QUEUE:
//                    doRoom(param);
                            break;
                        case MOVE:
//                    doActivate(param);
                            break;
                        case PASS:
//                    doHelp();
                            break;
                        case QUIT:
                            quit = true;
                            break;
                        default:
                            System.out.println("The input is not correct.");
                            break;
                    }
                }
            } catch (IOException e) {
                close();
                break;
            }
        }
    }

    public void doWelcome() {

    }


}


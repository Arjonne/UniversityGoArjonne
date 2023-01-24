package com.nedap.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the server for the GO game.
 */
public class Server implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private Thread socketThread;
    private boolean accepting = false;
    private Queue<ClientHandler> handlers;

    /**
     * Creates the server to be able to play the game.
     *
     * @param port is the port number that is needed to be able to connect to the server
     */
    public Server(int port) {
        this.port = port;
        this.handlers = new LinkedList<>();
    }

    /**
     * Starts the server. Server can only start when the server is not accepting connections yet, and when the port
     * number is a valid number, a new serverSocket should start with this port as input. A new socketThread should
     * be created and started too, and the boolean accepting should be set on true as the server is now accepting
     * connections.
     */
    public void start() {
        if (isAccepting()) {
            System.out.println("Server is already in use.");
            return; // stop want server is al in gebruik.
        }
        if (port < 0 || port > 65535) {
            System.out.println(port + " is not a valid port number");
            return; // stop want port bestaat niet.
        } else {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.out.println("Cannot connect to the server.");
            }
        }
        socketThread = new Thread(this);
        socketThread.start();
        accepting = true;
    }

    /**
     * Returns the port of the server. This method returns the actual port the server is accepting connections on.
     *
     * @return the port number, between 0 and 65535. Return -1 if the server is not accepting connections and local
     * port could not be found.
     */
    public int getPort() {
        if (isAccepting()) {
            return serverSocket.getLocalPort();
        }
        return -1;// if the server is not accepting, it is not active, so it can not find the local port --> return -1
    }

    /**
     * Stops the server. To be able to stop the server, it should have started in the first place. If that is true, all
     * active clientHandlers will be closed, and then the serverSocket will be closed as well. Next, the serverSocket
     * will join the main thread. If that all is executed correctly, accepting will be set to false again.
     */
    public void stop() {
        if (!isAccepting()) {
            System.out.println("The server did not even accept connections yet");
            return;
        }
        // closes all clientHandlers in the queue of handlers that currently handle clients that are connected to the server:
        while (!handlers.isEmpty()) {
            handlers.poll().close();
        }
        // after all clientHandlers are closed, try to close the serverSocket as well:
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server has already stopped or not even started at all.");
            throw new RuntimeException(e);
        }
        // after the serverSocket is closed, try to join the socketThread with the main thread:
        try {
            socketThread.join();
        } catch (InterruptedException e) {
            System.out.println("Cannot join main thread.");
            throw new RuntimeException(e);
        }
        // when the socketThread has joined the main thread, the server is closed and not accepting any connections anymore:
        accepting = false;
        System.out.println("Server is closed.");
    }

    /**
     * Checks whether the server is currently accepting connections.
     *
     * @return true if the server is accepting connections, false if not
     */
    public boolean isAccepting() {
        return accepting;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                addClient(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Not able to make a connection between server and client handler OR connection is already closed.");
        }
        System.out.println("Server has been closed");
    }

    /**
     * Adds the clientHandler of a newly connected client to the queue of connected clientHandlers.
     *
     * @param clientHandler is the new clientHandler of the newly connected client
     */
    public synchronized void addClient(ClientHandler clientHandler) {
        handlers.add(clientHandler);
    }

    /**
     * Removes the clientHandler from the queue of connected clientHandlers when the connection with the Client is closed.
     *
     * @param clientHandler is the clientHandler of the closed connection with the client
     */
    public synchronized void removeClient(ClientHandler clientHandler) {
        handlers.remove(clientHandler);
    }
}

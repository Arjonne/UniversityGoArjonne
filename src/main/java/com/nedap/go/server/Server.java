package com.nedap.go.server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents the server that hosts the GO game.
 */
public class Server implements Runnable {
    //TODO check what needs to be global and what is a local variable!
    private final int port;
    private ServerSocket serverSocket;
    private Thread socketThread;
    private boolean isOpen;
    private final List<ClientHandler> handlers;
    private final List<String> usernames;
    private final Queue<ClientHandler> waitingQueue;

    // Methods needed to start and stop the ability to connect to the server:

    /**
     * Creates the server to be able to play the game.
     *
     * @param port is the port number that is needed to be able to connect to the server
     */
    public Server(int port, InetAddress address) {
        this.port = port;
        // a new list is created that stores all clientsHandlers that are created to be able to communicate to clients:
        this.handlers = new ArrayList<>();
        // a new list is created that stores all usernames to be able to avoid double use of the same username:
        this.usernames = new ArrayList<>();
        // a queue is created in which clients (players) who want to play Go can wait until a second player is available:
        this.waitingQueue = new LinkedList<>();
        // after creating this server, it is not opened yet:
        isOpen = false;
    }

    /**
     * Starts the server. Server can only be started when the server is not open for connections yet. When the port
     * number is a valid number, a new serverSocket should start with this port as input. A new socketThread should
     * be created and started too, and the boolean isOpen should be set on true as the server is now open for
     * connections.
     */
    public void start() {
        if (isOpenForConnection()) {
            System.out.println("Server is already in use.");
            return; // stop as server is already in use.
        }
        if (port < 0 || port > 65535) {
            System.out.println(port + " is not a valid port number");
            return; // stop as port does not exist.
        } else {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.out.println("Not able to start the server with this port.");
            }
        }
        socketThread = new Thread(this);
        socketThread.start();
        isOpen = true;
    }

    /**
     * Returns the port on which a client can connect with this server. This method returns the actual port the server
     * is accepting connections on (so when 0 is used to get a random available port, this method returns the actual
     * port).
     *
     * @return the port number, between 0 and 65535. Return -1 if the server is not accepting connections and local
     * port could not be found.
     */
    public int getPort() {
        if (isOpenForConnection()) {
            return serverSocket.getLocalPort();
        }
        return -1;// if the server is not accepting, it is not active, so it can not find the port number --> return -1
    }

    /**
     * Stops the server. To be able to stop the server, it should have started in the first place. If that is true, all
     * active clientHandlers will be closed, and then the serverSocket will be closed as well. Next, the serverSocket
     * will join the main thread. If that all is executed correctly, accepting will be set to false again.
     */
    public void stop() {
        if (!isOpenForConnection()) {
            System.out.println("The server is not open for connections yet");
            return;
        }
        // closes all clientHandlers in the list of handlers that currently handle clients that are connected to the server:
        while (!handlers.isEmpty()) {
            handlers.get(0).close();
        }
        // after all clientHandlers are closed, try to close the serverSocket as well:
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Server has already stopped or was not even opened at all.");
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
        isOpen = false;
        System.out.println("Server is closed.");
    }

    /**
     * Checks whether the server is currently accepting connections.
     *
     * @return true if the server is accepting connections, false if not
     */
    public boolean isOpenForConnection() {
        return isOpen;
    }

    /**
     * Runs this operation. As long as the serverSocket is not closed (i.e. the server is not closed), it is accepting
     * new connections. When a client wants to connect, a new clientHandler is created and added to the list of active
     * clientHandlers.
     */
    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                addClientHandler(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Not able to make a connection between server and client via clientHandler OR server is just closed.");
        }
    }

    // Methods to add and remove clientHandler of connected client to a list to keep track of all connections:

    /**
     * Adds the clientHandler of a newly connected client to the list of connected clientHandlers.
     *
     * @param clientHandler is the new clientHandler of the newly connected client.
     */
    public synchronized void addClientHandler(ClientHandler clientHandler) {
        handlers.add(clientHandler);
    }

    /**
     * Removes the clientHandler from the list of connected clientHandlers when the connection with the Client is closed.
     *
     * @param clientHandler is the clientHandler of the closed connection with the client.
     */
    public synchronized void removeClientHandler(ClientHandler clientHandler) {
        handlers.remove(clientHandler);
    }

    // Methods to add, remove and get all usernames of players using clients connected via clientHandlers to this
    // server:

    /**
     * Adds the username of a newly connected client to the list of connected clients (players).
     *
     * @param username is the username this client (player) uses.
     */
    public synchronized void addUsername(String username) {
        if (!usernames.contains(username)) {
            usernames.add(username);
        }
    }

    /**
     * Removes the username from the list of connected clients when the connection with this Client is closed.
     *
     * @param username is the username of client that has ended the connection.
     */
    public synchronized void removeUsername(String username) {
        usernames.remove(username);
    }

    /**
     * Gets the list of all usernames that are currently in use.
     *
     * @return the list of all usernames
     */
    public synchronized List<String> getListOfUsernames() {
        return usernames;
    }

    // Methods to add, remove and get all clientHandlers of players using clients connected via these clientHandlers
    // to the queue to be able to wait for playing a game:

    /**
     * Adds the client to the queue to wait for another client to be able to play a GO game.
     *
     * @param clientHandler is the clientHandler of the client that enters the queue.
     */
    public synchronized void addToQueue(ClientHandler clientHandler) {
        waitingQueue.add(clientHandler);
    }

    /**
     * Removes the client from the queue (which thus stops waiting for another client to be able to play a GO game).
     *
     * @param clientHandler is the clientHandler of the client that leaves the queue.
     */
    public synchronized void removeFromQueue(ClientHandler clientHandler) {
        waitingQueue.remove(clientHandler);
    }

    /**
     * Gets the queue in which clients connecting via clientHandlers to this server are waiting to play a GO game.
     *
     * @return the queue with waiting clients for playing a GO game.
     */
    public Queue<ClientHandler> getWaitingQueue() {
        return waitingQueue;
    }
}

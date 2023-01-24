package com.nedap.go.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the client for the GO game.
 */
public class Client implements Runnable {
    //TODO check what needs to be global and what is a local variable!
    private Socket socket;
    private Queue<Listener> listeners = new LinkedList<>();
    private BufferedReader input;
    private PrintWriter writer;

    //TODO find out why no constructor for Client class was used in Chat exercise?

    /**
     * Connects the client to the server.
     *
     * @param address is the network address of the server
     * @param port    is the port number on which the server is accepting a connection
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

    //TODO look in Chat exercise how client commands are handled here.

    /**
     * Adds a listener for this client to the queue of listeners.
     *
     * @param listener is the listener to add
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for this client from the queue of listeners.
     *
     * @param listener is the listener to remove
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
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
}

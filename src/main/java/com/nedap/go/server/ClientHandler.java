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
     * queue of connected clients.
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
//        try {
//            USERNAME = input.readLine();
//        } catch (IOException e) {
//            close();
//            return;
//        }
//        while (!socket.isClosed()) {
//            try {
//                String line = input.readLine();
//                if (line == null) {
//                    close();
//                    break;
//                }
//                if (line.contains("SAY~")) { // this is exactly what the server needs to do with the server commands!!!!
//                    // add the actual methods (like handleChatMessage) to the server class!!!!! Look for it in Chat example.
//                    String[] arrOfStr = line.split("~", 2);
//                  //  server.handleChatMessage(this, arrOfStr[1]);
//                }
//            } catch (IOException e) {
//                close();
//                break;
//            }
//        }
    }
}

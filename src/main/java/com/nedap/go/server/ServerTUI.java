package com.nedap.go.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents the textual user interface for starting the server and handling the input from clients via clientHandlers.
 */
public class ServerTUI {
    public static void main(String[] args) {
        // let the server know on which port you want to start a new server
        System.out.println("Import a port number between 1 and 65535. Import 0 for a random available port.");
        Scanner scanner = new Scanner(System.in);
        try {
            int port = scanner.nextInt();
            while (port < 0 || port > 65535) {
                System.out.println("Port number should be between 1 and 65535; use 0 for a random available port.");
                port = scanner.nextInt();
            }
            // create and start a new server with the port input from above
            Server server = new Server(port, InetAddress.getLocalHost());
//            Server server = new Server(port, InetAddress.getByName("192.168.8.102"));
            server.start();
            System.out.println("Port number on which this server is accepting clients is: " + server.getPort());
            System.out.println("If you want to stop the server, type QUIT.");
            // server works until quit is used. Until then, all input from clients (via clientHandlers) will be handled via
            // run() methods in the clientHandler class
            boolean quit = false;
            while (!quit) {
                if (scanner.nextLine().equals("quit") || scanner.nextLine().equals("QUIT")) {
                    server.stop();
                    quit = true;
                }
            }
            // handle exception when port number input was not a number:
        } catch (InputMismatchException e) {
            System.out.println("No valid input: port number should be between 1 and 65535; use 0 for a random available port.");
        } catch (UnknownHostException e) {
            System.out.println("Server address could not be found.");
        }
    }
}

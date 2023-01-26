package com.nedap.go;

import com.nedap.go.client.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientTUI {

    public static void main(String[] args) {
        // Let your program ask for a server address, a server port and a username.
        System.out.println("Import a server address, a server port number and a username.");
        Scanner scanner = new Scanner(System.in);
        // enter localhost and copy the port that is the output of ServerApplication to connect the server and client:
        System.out.println("First, enter the server address: ");
        try {
            InetAddress address = InetAddress.getByName(scanner.nextLine());
            System.out.println("Now, enter the port number to connect to the server: ");
            int port = scanner.nextInt();
            Client client = new Client();
            if (client.connect(address, port)) {
                client.doHello(client.toString());
            }




        } catch (UnknownHostException e) {
            System.out.println("No IP address for the host could be found so connection is not established.");
        }
    }
}

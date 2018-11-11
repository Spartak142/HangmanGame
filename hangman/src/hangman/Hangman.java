package hangman;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Spartak142
 */
public class Hangman extends Thread {

    private static ServerSocket serverSocket;
    private static final int PORT = 1234;

    /**
     * @param args the command line arguments
     */
    /*Servers main class that creates a socket on the servers side that clients can connect to. 
Then it accepts all incoming clients and creates an instance of the client handler class for each of the clients */
    public static void main(String[] args) throws IOException {
        System.out.println("Opening port");

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioEx) {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
        do {
            Socket client = serverSocket.accept();
            System.out.println("New Client Accepted");
            ClientHandler handler = new ClientHandler(client);
            handler.start();
        } while (true);
    }

}

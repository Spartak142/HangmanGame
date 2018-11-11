package hangmanclient;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Spartak142
 */
public class Hangmanclient {

    /**
     * @param args the command line arguments
     */
    private static InetAddress host;
    private static final int PORT = 1234;
    private static Socket socket;
    private static volatile AtomicBoolean isFinished = new AtomicBoolean();

    // Main class currently set to connect to the server lcoated at Local host on port 1234
    public static void main(String[] args) throws IOException {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("Host ID not found");
            System.exit(1);
        }
//Method that contains the client program     
        sendMessages();
    }

    private static void sendMessages() {
        //Attempt to connect to the server
        try {
            socket = new Socket(host, PORT);
            Scanner networkInput = new Scanner(socket.getInputStream());
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

        // Since the client has to be multithreaded this thread is set to only listen to the user input and send it to the server
        final Thread outThread = new Thread() {
            @Override
            public void run() {
                //Explaining rules of the game
                System.out.println(" Thank you for playing my Hangman game!" + "\n" + "The rules are very simple. The mastermind will choose a random word and you will have to guess it.");
                System.out.println(" You can either guess it letter by letter (to do that, simply type the letter), or by typing in the whole word right away");
                System.out.println(" If you guess a letter correctly it will appear in all the places it occurs in the word, and if you don't, the amount of remaining attempts will decrease.");
                System.out.println(" Type 'Start' to start a new game session and reset the score." + "\n" + " Alternatively, Type 'Quit' to exit the program.");
                System.out.println(" Dear user, this program is NoT case sensitive, so do not worry about it! Please do not forget to hit 'Enter' after every guess or command");
                PrintWriter out = null;
                Scanner sysIn = new Scanner(System.in);
                //Try to set up Printwriter that will send input from the user to the server
                //To avoid a variety of errors the threads are synchronised. 
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.flush();
                    while (sysIn.hasNext() && !isFinished.get()) {
                        String line = sysIn.nextLine();
                        if ("Quit".equalsIgnoreCase(line)) {
                            synchronized (isFinished) {
                                isFinished.set(true);
                            }
                        }
                        out.println(line);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        };
        outThread.start();
// And this thread is set up to listen to messages from the server and then to proces them
        final Thread inThread = new Thread() {
            @Override
            public void run() {
                // Use a Scanner to read from the remote server
                Scanner in = null;
                try {
                    in = new Scanner(socket.getInputStream());
                    String response = in.nextLine();
                    while (!isFinished.get()) {
                        //Processing the response
                        String[] obtainedResponse = response.split("&");
                        if (obtainedResponse[3].equals("false")) {
                            System.out.println(" The current word is: " + obtainedResponse[1]);
                            System.out.println(" Your current score is " + obtainedResponse[0]);
                            System.out.println(" You have " + obtainedResponse[2] + " attempts left on the current word");
                        } else if (!obtainedResponse[2].equals("0") && obtainedResponse[2].toCharArray()[0] != '-') {
                            System.out.println(" Congratulations!!! You beat the game!!!");
                            System.out.println(" Your current score is: " + obtainedResponse[0]);
                            System.out.println(" Type 'START' to reset the score and start over, alternatively type 'NEXT' to play another match or 'QUIT' to exit the game");
                        } else {
                            System.out.println(" Too bad you are out of attempts... Care for another one?");
                            System.out.println(" Your current score is: " + obtainedResponse[0]);
                            System.out.println(" Type 'START' to reset the score and start over, alternatively type 'NEXT' to play another match or 'QUIT' to exit the game");
                        }
                        response = in.nextLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        ;
        };
        inThread.start();
    }
}

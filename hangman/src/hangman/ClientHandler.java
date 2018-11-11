package hangman;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Spartak142
 */
class ClientHandler extends Thread {

    private Socket client;
    private Scanner input;
    private PrintWriter output;
    //Dictionary list
    private ArrayList<String> dictionary = new ArrayList<String>();
    // char array to hold the word
    private char[] magicWord;
    // Char array to hold the game state
    private char[] hiddenWord;
    //Keeps track of user's score and lifes
    private int score;
    private int life;
    //Currently selected word
    private String word;
    //True when the word is guessed or a client is out of lives
    private Boolean gameOver;

    //Client Handler class contains the whole game logic
    public ClientHandler(Socket socket) {
        client = socket;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
            //method readfile is defined at the end. it just processes the dictionary document storing all the words into attay lsit dictionary.
            readFile();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    @Override
    public void run() {
        String received;
        //Get a message from a client and process it
        received = input.nextLine();
        // As long as a client does not ask to quit the game is on
        while (!received.equalsIgnoreCase("QUIT")) {
            /* These client commands make server create a new word. We can say that this is an easy version of the game where a player can skip aword by typing next.
        one can of course add a check on gameOver and if it is false then the game does not take a new word. The easy version is more friendly since playing a game versus 2 or 3 letter word is very hard*/
            if (received.equalsIgnoreCase("NEXT") || received.equalsIgnoreCase("START")) {
                gameOver = false;
                word = dictionary.get((int) (Math.random() * dictionary.size()));
                life = word.length();
                System.out.println(word);
                magicWord = word.toCharArray();
                hiddenWord = setup(word);
                //Calling start will reset the score as well
                if (received.equalsIgnoreCase("START")) {
                    score = 0;
                }
                MessageToSend messageToSendStart = new MessageToSend(hiddenWord, life, score, gameOver);
                output.println(messageToSendStart.toString());
            } else {
// Here is the logic to check whether a letter/word guessed correctly. The game also changes the score and amount of attempts left (life) acccordingly.
                boolean guessed = false;
                if (received.length() == 1) {
                    received.toLowerCase();
                    for (int i = 0; i < magicWord.length; i++) {
                        if (magicWord[i] == received.charAt(0)) {
                            hiddenWord[i] = received.charAt(0);
                            guessed = true;
                        }
                    }
                    if (guessed == false) {
                        life = life - 1;
                    }
                } else {
                    if (word.equalsIgnoreCase(received)) {
                        hiddenWord = received.toCharArray();
                    } else {
                        life = life - 1;
                    }
                }
                if (life == 0 && gameOver == false) {
                    score--;
                    gameOver = true;
                }
                if (!(new String(hiddenWord).contains("_")) && gameOver == false) {
                    score++;
                    gameOver = true;
                }
                /*Class message to send is defined in MessageToSend.java, it consists of a char array
           (the word, two integers (score and life) and a boolean variable to check if the match is over. 
           The first line makes the sent message visible in the servers command window*/
                System.out.println(new MessageToSend(hiddenWord, life, score, gameOver).toString());
                output.println(new MessageToSend(hiddenWord, life, score, gameOver).toString());
            }

            //Take in the next guess/ user command
            received = input.nextLine();
        }
        //Closing connection after session is over
        try {
            if (client != null) {
                System.out.println("Closing down connection...");
                client.close();
            }
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect");
        }
    }
//Processing a dictionary which is located in a file words.txt and getting rid of the single letters.

    public ArrayList<String> readFile() throws IOException {
// The path is specific to my directories and it has to be changed accordinly
        File possibleWords = new File("C:\\Users\\MVPIMP\\Documents\\NetBeansProjects\\hangman\\words.txt");
        Scanner wordInput = new Scanner(possibleWords);
        while (wordInput.hasNext()) {
            String add = wordInput.next();
            if (add.length() != 1) {
                this.dictionary.add(add.toLowerCase());
            }
        }
        return dictionary;
    }

// Used to hide all the letters in the beginning    
    public static char[] setup(String word) {
        int lengthOfWord = word.length();
        char[] trial = new char[lengthOfWord];
        for (int i = 0; i < lengthOfWord; i++) {
            trial[i] = '_';
        }
        return trial;
    }
}

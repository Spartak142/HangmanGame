package hangman;

/**
 *
 * @author Spartak142
 */
// Message to send is a data type that is used to be sent to the client
public class MessageToSend {

    public char[] array1;
    public int life;
    public int score;
    public Boolean done;

    // BAsic constructor
    public MessageToSend(char[] word, int li, int sc, Boolean don) {
        this.array1 = word;
        this.life = li;
        this.score = sc;
        this.done = don;
    }

    @Override
    // TO string method puts all the values in a string, as well as separates them with & so that they can be processed by clients by simple string split.
    public String toString() {
        String result;
        result = score + "&" + toString(array1) + "&" + life + "&" + done;
        return result;
    }
    // Method to process char arrays into strings and separate the letters to make it easier for the user to see letters hidden with _

    public static String toString(char[] array) {
        String result = " ";
        for (int i = 0; i < array.length; i++) {
            result = result + array[i] + " ";
        }
        return result;
    }
}

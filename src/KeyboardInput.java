// import declaration
import java.util.Scanner;

// class declaration
public final class KeyboardInput {
    private static KeyboardInput referenceToSingleInputObject = null;

    // for input storage
    private Scanner scannerKeyboard;

    // initializer
    private KeyboardInput() {
        scannerKeyboard = new Scanner(System.in);
    }

    // returns KeyboardInput instance
    public static KeyboardInput getInstance(){
        if (referenceToSingleInputObject == null){
            referenceToSingleInputObject = new KeyboardInput();
        }
        return referenceToSingleInputObject;
    }

    /**
     * Reads an integer from the user with a given prompt.
     *
     * @param prompt The message to display as a prompt.
     * @return The integer entered by the user.
     */
    public int getInt(String prompt) {
        System.out.print(prompt);
        String intergerS = scannerKeyboard.nextLine();
        // converting to integer
        return Integer.parseInt(intergerS);
    }

    /**
     * Reads a string from the user with a given prompt.
     *
     * @param prompt The message to display as a prompt.
     * @return The string entered by the user.
     */
    public String getString(String prompt) {
        System.out.print(prompt);
        // capture all input until enter key
        scannerKeyboard.useDelimiter("\r\n");
        String sInput = scannerKeyboard.nextLine();
        return sInput;
    }

}

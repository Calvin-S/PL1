package Parser;

/** An exception indicating a syntax error. */
@SuppressWarnings("serial")
public class SyntaxError extends Exception {
    public SyntaxError(String message) {
        super(message);
    }
}
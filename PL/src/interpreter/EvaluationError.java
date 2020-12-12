package interpreter;

/** An exception indicating a syntax error. */
@SuppressWarnings("serial")
public class EvaluationError extends Exception {
	public EvaluationError(String message) {
		super(message);
	}
}
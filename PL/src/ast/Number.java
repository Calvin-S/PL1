package ast;

/** A representation of the <Number> node. */
public class Number extends AExpr {

	private int value;

	/**
	 * Creates a new Number representing the value of v.
	 * 
	 * @param v The value the Number represents.
	 */
	public Number(int v) {
		value = v;
	}

	public int getNum() {
		return value;
	}

	public Number clone() {
		return new Number(value);
	}

	public String toString() {
		return "" + value;
	}

}

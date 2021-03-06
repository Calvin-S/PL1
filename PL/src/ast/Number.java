package ast;

/** A representation of the <Number> node. */
public class Number extends AExpr {

	private long value;

	/**
	 * Creates a new Number representing the value of v.
	 * 
	 * @param v The value the Number represents.
	 */
	public Number(long v) {
		super();
		value = v;
	}

	public long getNum() {
		return value;
	}

	public Number clone() {
		return new Number(value);
	}

	public String toString() {
		return "" + value;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(toString());
		return sb;
	} 
	
	@Override
	public String nodeType() {
		return "num";
	}
}

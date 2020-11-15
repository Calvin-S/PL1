package ast;

public class Bool extends BExpr{
	private boolean value;

	/**
	 * Creates a new Number representing the value of v.
	 * 
	 * @param v The value the Number represents.
	 */
	public Bool(boolean v) {
		super();
		value = v;
	}

	public boolean getBool() {
		return value;
	}

	public Bool clone() {
		return new Bool(value);
	}

	public String toString() {
		return "" + value;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(toString());
		return sb;
	} 
}

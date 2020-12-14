package ast;

public class Str extends Type{
	private String value;
	public Str(String v) {
		super();
		value = v;
	}

	public String getString() {
		return value;
	}

	public Str clone() {
		return new Str(value);
	}

	public String toString() {
		return "\"" + value + "\"";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(toString());
		return sb;
	} 
}

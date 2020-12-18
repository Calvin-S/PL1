package ast;

public class ListSize extends Type{
	private Type l;

	public ListSize(Type l) {
		this.l = l;
	}

	public String toString() {
		return prettyPrint(new StringBuilder()).toString();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("size(");
		l.prettyPrint(sb);
		sb.append(")");
		return sb;
	} 
}

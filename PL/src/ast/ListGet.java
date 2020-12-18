package ast;

public class ListGet extends Type{
	private Type list;
	private Expr index;
	
	public ListGet(Type list, Expr index) {
		this.list = list;
		this.index = index;
	}
	
	public Expr getList() {
		return list;
	}
	
	public Expr getIndex() {
		return index;
	}
	
	public String toString() {
		return prettyPrint(new StringBuilder()).toString();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("get(");
		list.prettyPrint(sb);
		if (index != null) {
			sb.append(", ");
			index.prettyPrint(sb);
		}
		sb.append(")");
		return sb;
	} 
}

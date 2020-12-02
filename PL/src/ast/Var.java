package ast;

public class Var extends Type{
	private String name;
	private Expr e;
	
	public Var(String name, Expr e) {
		this.name = name;
		this.e = e;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(name + " = ");
		if (e != null)
			e.prettyPrint(sb);
		return sb;
	} 
	
	public String toString() {
		if (e == null)
			return name;
		return name + " = " + e.toString();
	}
	
	public String nodeType() {
		return "BExpr";
	}
}

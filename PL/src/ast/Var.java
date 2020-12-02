package ast;

public class Var extends Type{
	private String name; 
	private Expr e;
	private boolean getValue; // if True then should find value of this variable
	
	public Var(String name, Expr e) {
		getValue = false;
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


	public String getName() {
		return name;
	}

	public void setAsValue() {
		getValue = true;
	}
	
	public boolean isValue() { return getValue; }

}

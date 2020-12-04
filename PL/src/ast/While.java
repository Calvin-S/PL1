package ast;

import java.util.ArrayList;
import java.util.List;

public class While extends Expr{
	private Type guard;
	private Seq body;
	
	public While(Type guard) {
		this.guard = guard;
		body = new Seq();
	}
	
	public void addBranch(Seq branch) {
		body = branch;
	}

	public Node getGuards() {
		return guard;
	}

	public Seq getBody() {
		return body;
	}
	
	public String toString() {
		String s = "{While " + guard;
		s += " do " + body + "}";
		return s;
	}
	
	public String nodeType() {
		return "WHILE";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("While " + guard + " do:\n");
		sb.append("  " + body + "\n");
		return sb;
	} 
}

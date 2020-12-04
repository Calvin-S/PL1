package ast;

import java.util.ArrayList;
import java.util.List;

public class While extends Expr{
	private Type guard;
	private ArrayList<Expr> body;
	
	public While(Type guard) {
		this.guard = guard;
		body = new ArrayList<Expr>();
	}
	
	public void addBranch(Expr branch) {
		body.add(branch);
	}

	public Node getGuards() {
		return guard;
	}

	public List<Node> getBranches() {
		List<Node> g = new ArrayList<Node>();
		for (Expr b : body) {
			g.add(b);
		}
		return g;
	}
	
	public String toString() {
		String s = "{ While " + guard;
		for (int i = 0; i < body.size(); i++) {
			s += " do " + body.get(i);
		}
		s += "}";
		return s;
	}
	
	public String nodeType() {
		return "WHILE";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("While " + guard + " do:\n");
		for (int i = 0; i < body.size(); i++) {
			sb.append("  " + body.get(i));
			sb.append("\n");
		}
		return sb;
	} 
}

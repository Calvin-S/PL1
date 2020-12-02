package ast;
import java.util.ArrayList;
import java.util.List;

public class If extends Expr{
	private ArrayList<BExpr> guards;
	private ArrayList<Expr> branches;
	
	public If(BExpr guard, Expr branch) {
		guards = new ArrayList<BExpr>();
		branches = new ArrayList<Expr>();
		guards.add(guard);
		branches.add(branch);
	}
	
	public void addBranch(BExpr guard, Expr branch) {
		assert guards != null && branches != null;
		guards.add(guard);
		branches.add(branch);
		assert guards.size() == branches.size();
	}

	public List<Node> getGuards() {
		List<Node> g = new ArrayList<Node>();
		for (BExpr b : guards) {
			g.add(b);
		}
		return g;
	}

	public List<Node> getBranches() {
		List<Node> g = new ArrayList<Node>();
		for (Expr b : branches) {
			g.add(b);
		}
		return g;
	}
	
	public String toString() {
		assert guards != null && branches != null && guards.size() == branches.size();
		String s = "{";
		for (int i = 0; i < branches.size(); i++) {
			s += " If (" + guards.get(i) + ") then " + branches.get(i);
		}
		s += "}";
		return s;
	}
	
	public String nodeType() {
		return "IF";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		assert guards != null && branches != null && guards.size() == branches.size();
		for (int i = 0; i < branches.size(); i++) {
			if (i == 0)
				sb.append("If (" + guards.get(i) + ") then " + branches.get(i));
			else
				sb.append("Else If (" + guards.get(i) + ") then " + branches.get(i));
			sb.append("\n");
		}
		return sb;
	} 
	
}
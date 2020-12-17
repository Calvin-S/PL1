package ast;
import java.util.ArrayList;
import java.util.List;

public class If extends Expr{
	private ArrayList<Type> guards;
	private ArrayList<Seq> branches;
	
	public If(Type guard, Seq branch) {
		guards = new ArrayList<Type>();
		branches = new ArrayList<Seq>();
		guards.add(guard);
		branches.add(branch);
	}
	
	public void addBranch(Type guard, Seq branch) {
		assert guards != null && branches != null;
		guards.add(guard);
		branches.add(branch);
		assert guards.size() == branches.size();
	}

	public List<Node> getGuards() {
		List<Node> g = new ArrayList<Node>();
		for (Type b : guards) {
			g.add(b);
		}
		return g;
	}

	public List<Node> getBranches() {
		List<Node> g = new ArrayList<Node>();
		for (Seq b : branches) {
			g.add(b);
		}
		return g;
	}
	
	public String toString() {
		assert guards != null && branches != null && guards.size() == branches.size();
		String s = "{";
		for (int i = 0; i < branches.size(); i++) {
			s += "If " + guards.get(i) + " then (" + branches.get(i) + ")";
			if (i != branches.size() - 1)
				s += "\n";
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
				sb.append("If " + guards.get(i) + " then " + branches.get(i));
			else
				sb.append("Else If " + guards.get(i) + " then " + branches.get(i));
		}
		return sb;
	} 
	
}
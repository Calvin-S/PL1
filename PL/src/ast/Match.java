package ast;
import java.util.ArrayList;
public class Match extends Expr{
	private String toMatchVar;
	private ArrayList<String> types;
	private ArrayList<Expr> branches;
	
	public Match(String v) {
		types = new ArrayList<String>();
		branches = new ArrayList<Expr>();
		toMatchVar = v;
	}
	
	public void addBranch(String typeName, Expr e) {
		types.add(typeName);
		branches.add(e);
		assert types.size() == branches.size();
	}
	
	public ArrayList<String> getMatchTypes() {
		return types;
	}
	
	public ArrayList<Expr> getBranches() {
		return branches;
	}
	
	public String toString() {
		assert types != null && branches != null && types.size() == branches.size();
		return prettyPrint(new StringBuilder()).toString();
	}
	
	public String nodeType() {
		return "Match";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		assert types != null && branches != null && types.size() == branches.size();
		sb.append("{Match " + toMatchVar + " with: \n");
		for (int i = 0; i < branches.size(); i++) {
			sb.append(types.get(i) + ": " + branches.get(i));
			if (i != branches.size() - 1)
				sb.append("\n");
		}
		sb.append("}");
		return sb;
	} 
	
	
}

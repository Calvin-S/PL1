package ast;

import java.util.ArrayList;
import java.util.List;

public class Seq extends AbstractNode{
	private List<Expr> sequence;
	public Seq() {
		sequence = new ArrayList<Expr>();
	}
	
	public List<Expr> getSeq() {
		return sequence;
	}
	
	public void addToSeq(Expr e) {
		if (e != null)
			sequence.add(e);
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < sequence.size(); i++) {
			if (i != sequence.size() - 1)
				s += sequence.get(i) + "; ";
			else
				s += sequence.get(i);
		}
		return s;
	}
	
	public String nodeType() {
		return "SEQ";
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		for (int i = 0; i < sequence.size(); i++) {
			sb.append(sequence.get(i) + "\n");
		}
		return sb;
	} 
}

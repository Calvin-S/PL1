package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** A data structure representing a critter program. */
public class Program extends AbstractNode {
	
	private ArrayList<Expr> children;

	public Program() {
		children = new ArrayList<Expr>();
	}

	/**
	 * Adds r to children of this.
	 * 
	 * @param r rule to add
	 */
	public void addNode(Expr e) {
		children.add(e);
	}

	public List<Node> getChildren() {
		List<Node> children_copy = new ArrayList<Node>();
		for (Expr e : children) { //note this doesn't actually clone the children
			children_copy.add(e);
		}
		return children_copy;
	}

	@Override
	public Node getParent() {
		return parent;
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("(");
		for (Expr e : children)
			e.prettyPrint(sb);
		sb.append(")");
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return prettyPrint(sb).toString();
	}
}

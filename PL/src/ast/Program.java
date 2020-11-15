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
		for (Expr e : children) {
			children_copy.add(e);
		}
		return children_copy;
	}
}

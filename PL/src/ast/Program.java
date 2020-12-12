package ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** A data structure representing a critter program. */
public class Program extends AbstractNode {
	
	private ArrayList<Fun> children;

	public Program() {
		children = new ArrayList<Fun>();
	}

	/**
	 * Adds r to children of this.
	 * 
	 * @param r rule to add
	 */
	public void addNode(Fun e) {
		children.add(e);
	}

	public List<Node> getChildren() {
		List<Node> children_copy = new ArrayList<Node>();
		for (Fun e : children) { //note this doesn't actually clone the children
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
		for (Fun e : children)
			e.prettyPrint(sb);
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return prettyPrint(sb).toString();
	}
}

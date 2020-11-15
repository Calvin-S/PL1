package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNode implements Node {
	Node parent = null;

	@Override
	public int size() {
		int size = 1;
		List<Node> children = getChildren();
		for (Node child : children) {
			size += child.size();
		}
		return size;
	}

	@Override
	public Node nodeAt(int index) {
		if (index == 0) {
			return this;
		}
		index--;
		List<Node> children = getChildren();
		for (Node child : children) {
			if (child.size() > index) {
				return child.nodeAt(index);
			} else {
				index -= child.size();
			}
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(this.toString());
		return sb;
	}

	@Override
	public List<Node> getChildren() {
		return new ArrayList<Node>();
	}

	public Node getParent(Node tree, Node toFind) {
		if (tree == toFind) {
			return parent;
		}

		Node current = tree;
		if (!(current.getChildren().isEmpty())) {
			for (Node child : current.getChildren()) {
				if (toFind == child) {
					parent = current;
				} else {
					getParent(child, toFind);
				}
			}
		}

		return parent;
	}

}

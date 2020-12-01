package ast;

import java.util.ArrayList;
import java.util.List;


public class BExpr extends Binop{
	
	private ExprOperator operator;
	private Binop left, right;

	protected BExpr() {} // For bool
	
	public BExpr(BExpr not) {
		operator = ExprOperator.NOT;
		right = not;
		left = null;
	}
    /**
	 * Create an AST representation of l op r.
	 *
	 * @param l  The expression to the left of the op.
	 * @param op The binary expression operation.
	 * @param r  The expression to the right of the op.
	 */
	public BExpr(BExpr l, ExprOperator op, BExpr r) {
		left = l;
		operator = op;
		right = r;
    }


	public List<Node> getChildren() {
		List<Node> children = new ArrayList<Node>();
		children.add(left);
		children.add(right);
		return children;
	}

	public String toString() {
		return "( " + left.toString() + " " + operator.toString() + " " + right.toString() + " )";
	}

	public String getOperator() {
		return operator.toString();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("(");
		if (left != null)
			left.prettyPrint(sb);
		sb.append(" " + operator.toString() + " ");
		right.prettyPrint(sb);
		sb.append(")");
		return sb;
	} 
	
	public String nodeType() {
		return "BExpr";
	}
}

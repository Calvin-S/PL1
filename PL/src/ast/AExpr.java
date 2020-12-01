package ast;

import java.util.ArrayList;
import java.util.List;

/** A representation of a binary operation between two expressions. */
public class AExpr extends BExpr{

	private Expr.ExprOperator operator;
	private AExpr left, right;

	protected AExpr() {} // For a number
    /**
	 * Create an AST representation of l op r.
	 *
	 * @param l  The expression to the left of the op.
	 * @param op The binary expression operation.
	 * @param r  The expression to the right of the op.
	 */
	public AExpr(AExpr l, Expr.ExprOperator op, AExpr r) {
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
		left.prettyPrint(sb);
		sb.append(" " + operator.toString() + " ");
		right.prettyPrint(sb);
		sb.append(")");
		return sb;
	} 
	
	@Override
	public String nodeType() {
		return "AExpr";
	}

}

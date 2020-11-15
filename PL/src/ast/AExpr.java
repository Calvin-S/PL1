package ast;

import java.util.ArrayList;
import java.util.List;

/** A representation of a binary operation between two expressions. */
public class AExpr extends Expr{

	private ExprOperator operator;
	private AExpr left, right;

    /**
	 * Create an AST representation of l op r.
	 *
	 * @param l  The expression to the left of the op.
	 * @param op The binary expression operation.
	 * @param r  The expression to the right of the op.
	 */
	public AExpr(AExpr l, ExprOperator op, AExpr r) {
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

	/** An enumeration of all possible binary expression operators. */
	public enum ExprOperator {
		PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), EXP("**");

		private final String stringRep;

		private ExprOperator(String s) {
			stringRep = s;
		}

		public String toString() {
			return stringRep;
		}
    }

	public String getOperator() {
		return operator.toString();
	}

}

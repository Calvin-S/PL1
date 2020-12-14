package ast;

import java.util.ArrayList;
import java.util.List;

import ast.Expr.ExprOperator;

public class StrExpr extends Type{
	private ExprOperator operator;
	private Type left, right;
	
	public StrExpr(Type rev) {
		operator = ExprOperator.REV;
		right = rev;
		left = null;
	}

	public StrExpr(Type l, ExprOperator op, Type r) {
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
		if (left == null)
			return "( " + operator.toString() + " " + right.toString() + " )";
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
		return "StrExpr";
	}
}

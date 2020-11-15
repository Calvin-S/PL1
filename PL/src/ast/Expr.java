package ast;

public class Expr extends AbstractNode {
	/** An enumeration of all possible binary expression operators. */
	public enum ExprOperator {
		PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), EXP("**"), 
		AND("and"), OR("or"), NOT("not"), EQ("=="), NEQ("!="), 
		GT(">"), LT ("<"), GTE(">="), LTE("<=");

		private final String stringRep;

		private ExprOperator(String s) {
			stringRep = s;
		}

		public String toString() {
			return stringRep;
		}
    }
}

package ast;

public class ListExpr extends Expr{
	public enum ListOperator {
		GET("get"), INSERT("insert"), REMOVE("remove"), REPLACE("replace");

		private final String stringRep;

		private ListOperator(String s) {
			stringRep = s;
		}

		public String toString() {
			return stringRep;
		}
    }
	
	private ListOperator operator;
	private Type list;
	private Expr index;
	private Expr value; // for when you insert something into list
	
	// For get and remove
	public ListExpr(ListOperator o, Type list, Expr index) {
		operator = o;
		this.index = index;
		this.list = list;
		value = null;
	}
	
	// For insertion and replace commands
	public ListExpr(ListOperator o, Type list, Expr index, Expr value) {
		operator = o;
		this.index = index;
		this.list = list;
		this.value = value;
	}
	
	public String getOperator() {
		return operator.toString();
	}
	
	public Expr getList() {
		return list;
	}
	
	public Expr getIndex() {
		return index;
	}
	
	public String toString() {
		return prettyPrint(new StringBuilder()).toString();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append(operator.toString() + "(");
		list.prettyPrint(sb);
		if (value != null && (operator.equals(ListOperator.INSERT) || operator.equals(ListOperator.REPLACE))) {
			sb.append(",");
			value.prettyPrint(sb);
		}
		if (index != null) {
			sb.append(", ");
			index.prettyPrint(sb);
		}
		sb.append(")");
		return sb;
	} 
}

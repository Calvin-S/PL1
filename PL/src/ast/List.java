package ast;
import java.util.ArrayList;

public class List extends Type{
	private ArrayList<Expr> values;

	public List() {
		values = new ArrayList<Expr>();
	}
	
	public void addValue(Expr t) {
		values.add(t);
	}

	public ArrayList<Expr> getValues() {
		return values;
	}

	public String toString() {
		return prettyPrint(new StringBuilder()).toString();
	}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("[");
		for (int i = 0; i < values.size(); i++) {
			sb.append(values.get(i).toString());
			if (i != values.size() - 1)
				sb.append(", ");
		}
		
		sb.append("]");
		return sb;
	} 
}

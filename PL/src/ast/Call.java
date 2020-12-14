package ast;
import java.util.ArrayList;

public class Call extends Expr{
	String funToCall;
	ArrayList<Type> args;
	public Call(String f) {
		funToCall = f;
		args = new ArrayList<Type>();
	}
	
	public void addArg(Type a) {
		args.add(a);
	}
	
	public String getFuncName() {
		return funToCall;
	}

	public ArrayList<Type> getArguments() {
		return args;
	}

	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("@" + funToCall + "(");
		for (Type e : args) {
			e.prettyPrint(sb);
			sb.append(" ");
		}
		sb.append(")");
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return prettyPrint(sb).toString();
	}
	
}

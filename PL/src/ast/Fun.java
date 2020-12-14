package ast;
import java.util.ArrayList;
import java.util.List;
public class Fun extends AbstractNode{
	private String name;
	private List<Var> args;
	private Seq body;
	private boolean isMain;
	public Fun() {
		name = "Main";
		isMain = true;
		args = new ArrayList<Var>();
	}
	public Fun(String name, ArrayList<Var> args) {
		this.name = name;
		isMain = false;
		if (args != null)
			this.args = args;
	}
	
	public void assignSeq(Seq s) {
		body = s;
	}
	
	public Seq getBody() {
		return body;
	}

	public String getName() {
		return name;
	}

	public List<Var> getArgs() {
		return args;
	}

	public boolean isMain() {return isMain;}
	
	@Override
	public StringBuilder prettyPrint(StringBuilder sb) {
		sb.append("Fun " + name + " (");
		for (int i = 0; i < args.size(); i++) {
			args.get(i).prettyPrint(sb);
			if (i != args.size() - 1) 
				sb.append(" ");
		}
		if (!isMain)
			sb.append(")\n[");
		else
			sb.append(")");
		body.prettyPrint(sb);
		if (sb.charAt(sb.length() - 1) == '\n')
			sb.setLength(sb.length() - 1);
		if (!isMain)
			sb.append("]\n");
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return prettyPrint(sb).toString();
	}
}

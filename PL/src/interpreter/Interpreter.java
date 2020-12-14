package interpreter;

import java.util.HashMap;
import java.util.List;

import ast.BExpr;
import ast.Bool;
import ast.Call;
import ast.Expr;
import ast.Fun;
import ast.If;
import ast.Node;
import ast.Null;
import ast.Number;
import ast.Program;
import ast.Seq;
import ast.Str;
import ast.StrExpr;
import ast.Var;
import ast.While;

public class Interpreter {

	private HashMap<String, Value> store = null;
	private HashMap<String, Fun> functions = null;

	public Interpreter() {
		store = new HashMap<String, Value>();
		functions = new HashMap<String, Fun>();
	}

	public Interpreter(HashMap<String, Value> parameters) {
		store = new HashMap<String, Value>();
		functions = new HashMap<String, Fun>();
		for (String key : parameters.keySet()) {
			store.put(key, parameters.get(key));
		}
	}
	
	public Interpreter(HashMap<String, Value> parameters, Fun fun) {
		store = new HashMap<String, Value>();
		functions = new HashMap<String, Fun>();
		for (String key : parameters.keySet()) {
			store.put(key, parameters.get(key));
		}
		functions.put(fun.getName(), fun);
	}

	public Value evaluateProg(Program p) throws EvaluationError {
		List<Node> funs = p.getChildren();

		Fun main = (Fun) funs.get(funs.size() - 1);

		for (int i = 0; i < funs.size(); i++) {
			Fun currentFun = (Fun) funs.get(i);
			functions.put(currentFun.getName(), currentFun);

			if (currentFun.isMain()) {
				main = currentFun;
			}
		}

		Seq seq = main.getBody();

		List<Expr> children = seq.getSeq();
		Value lastVal = null;
		for (int i = 0; i < children.size(); i++) {
			if (i == children.size() - 1) {
				lastVal = evaluateExpr(children.get(i));
			} else {
				evaluateExpr(children.get(i));
			}
		}

		return lastVal;

		// what value to return?
	}

	public Value evaluateFun(Fun p) throws EvaluationError {
		Seq seq = p.getBody();

		List<Expr> children = seq.getSeq();

		Value lastVal = null;

		for (int i = 0; i < children.size(); i++) {
			if (i == children.size() - 1) {
				lastVal = evaluateExpr(children.get(i));
			} else {
				evaluateExpr(children.get(i));
			}
		}

		return lastVal;

		// what value to return?
	}

	public HashMap<String, Value> getStore() {
		return store;
	}

	// evaluates a SINGLE line, not a whole program
	public Value evaluateExpr(Node n) throws EvaluationError {
		
		if (n instanceof Number) {

			Number r = (Number) n;
			return new Value(r.getNum());

		} else if (n instanceof Bool) {

			Bool r = (Bool) n;
			return new Value(r.getBool());

		} else if (n instanceof BExpr) { // this will include AExpr instances right?

			BExpr b = (BExpr) n;
			return evaluateBExpr(b);

		} else if (n instanceof StrExpr) {
			StrExpr b = (StrExpr) n;
			return evaluateStrExpr(b);

		} else if (n instanceof Str) {

			Str r = (Str) n;
			return new Value(r.getString());

		} else if (n instanceof If) {

			If r = (If) n;
			return evaluateIf(r);

		} else if (n instanceof Var) {

			Var r = (Var) n;
			return evaluateVal(r);

		} else if (n instanceof Null) {

			return new Value();

		} else if (n instanceof Call){
			Call r = (Call) n;
			String funName = r.getFuncName();
			List<ast.Type> args = r.getArguments();
			
			Fun fun = functions.get(funName);
			
			if(fun == null){
				throw new EvaluationError("You are trying to call a function that has not been defined");
			}
			List<Var> parameters = fun.getParam();
	
			if(args.size() != parameters.size()) {
				throw new EvaluationError("The number of arguments you gave does not match how many the function needs.");
			}
			
			HashMap<String, Value> passAlongParams = new HashMap<String, Value>();
			
			for(int i = 0; i<args.size(); i++) {
				passAlongParams.put(parameters.get(i).getName(), evaluateExpr(args.get(i)));

			}
			
			Interpreter funcInterpret = new Interpreter(passAlongParams, fun);
			return funcInterpret.evaluateFun(fun);
			
		} else if (n instanceof While) {
			While r = (While) n;
			
			boolean flag = true;
			Value vbody = null;
			
			while(flag) {
				Value vguard = evaluateExpr(r.getGuard());
				if(vguard.getType().equals("bool")) {
					if(vguard.getBool()) {
						Seq body = r.getBody();
						vbody = evaluateExpr(body);
					}else {
						if (vbody == null)
							vbody = new Value();
						flag = false;
					}
					
				}else {
					throw new EvaluationError("Guard is not a boolean");
				}
			}
			
			return vbody;
			
		} else if (n instanceof Seq) {

			List<Expr> children = ((Seq) n).getSeq();

			// assuming the only children are exprs, no functions or anything yet.

			Value lastVal = null;

			for (int i = 0; i < children.size(); i++) {
				if (i == children.size() - 1) {
					lastVal = evaluateExpr(children.get(i));
				} else {
					evaluateExpr(children.get(i));
				}
			}

			return lastVal;

		} else {
			System.out.println(n.getClass());
			throw new EvaluationError("the tree I got cannot be evaluated. Please check me.");
		}

	}
	
	public void printStore() {
		if (store != null) {
			for (String key : store.keySet()) {
				System.out.println(key + " : " + store.get(key));
			}
		} else {
			System.out.println("store was never initialized");
		}
	}

	public Value evaluateVal(Var r) throws EvaluationError {

		if (r.isValue()) {

			if (store.containsKey(r.getName())) {
				Value v = store.get(r.getName());
				if (v == null) {
					throw new EvaluationError("this variable does not have a value");
				}

				return store.get(r.getName());
			} else {
				printStore();
				System.out.println(r.getName());
				throw new EvaluationError("this variable does not exist");
			}

		} else {
			Value v;
			if (r.getChild() != null) {
				v = evaluateExpr(r.getChild());
				store.put(r.getName(), v);
			}
			else {
				v = new Value();
				store.put(r.getName(), v);
			}
			return v;
		}
	}

	public Value evaluateIf(If r) throws EvaluationError {

		List<Node> guards = r.getGuards();
		List<Node> branches = r.getBranches();

		Node toExecute = null;

		int i = 0;
		while (i < guards.size()) {
			Value v = evaluateExpr(guards.get(i));
			if (!(v.getType().equals("bool"))) {
				throw new EvaluationError("a guard I tried to evaluate is not a boolean");
			} else {
				if (v.getBool()) {
					toExecute = branches.get(i);
					break;
				} else {
					i++;
				}
			}
		}

		if (toExecute == null) {
			return new Value();
		}

		return evaluateExpr(toExecute);
	}

	public Value evaluateStrExpr(StrExpr b) throws EvaluationError {

		if (b.getOperator().equals("~")) {

			Value v = evaluateExpr(b.getChildren().get(1));

			if (v.getType().equals("string")) {
				StringBuilder revStr = new StringBuilder();
				revStr.append(v.getString());
				return new Value(revStr.reverse().toString());
			} else {
				throw new EvaluationError("calling reverse (~) on something that is not a string");
			}

		} else if (b.getOperator().equals("^")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("string") && v2.getType().equals("string")) {
				return new Value(v1.getString() + v2.getString());
			} else if (v1.getType().equals("string") && v2.getType().equals("int")) {
				return new Value(v1.getString() + Long.toString(v2.getInt()));
			} else if (v1.getType().equals("int") && v2.getType().equals("string")) {
				return new Value(Long.toString(v1.getInt()) + v2.getString());
			} else {
				throw new EvaluationError("trying to concatenate one or more things that are not strings/ints");
			}
		} else {
			throw new EvaluationError("invalid operator");
		}

	}

	public Value evaluateBExpr(BExpr b) throws EvaluationError {

		if (b.getOperator().equals("not")) {
			
			Value v = evaluateExpr(b.getChildren().get(1));

			if (v.getType().equals("bool")) {
				return new Value(!v.getBool());
			} else {
				throw new EvaluationError("calling NOT on something that is not a boolean");
			}

		} else if (b.getOperator().equals("+")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() + v2.getInt());
			} else if (v1.getType().equals("string") && v2.getType().equals("string")) {
				return new Value(v1.getString() + v2.getString());
			} else if (v1.getType().equals("string") && v2.getType().equals("int")) {
				return new Value(v1.getString() + Long.toString(v2.getInt()));
			} else if (v1.getType().equals("int") && v2.getType().equals("string")) {
				return new Value(Long.toString(v1.getInt()) + v2.getString());
			} else {
				throw new EvaluationError("trying to add one or more things that are not ints");
			}
		} else if (b.getOperator().equals("-")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() - v2.getInt());
			} else {
				throw new EvaluationError("trying to sub one or more things that are not ints");
			}

		} else if (b.getOperator().equals("*")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() * v2.getInt());
			} else {
				throw new EvaluationError("trying to mult one or more things that are not ints");
			}

		} else if (b.getOperator().equals("/")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				if (v2.getInt() != 0) {
					return new Value(v1.getInt() / v2.getInt());
				} else {
					throw new EvaluationError("cannot divide by 0");
				}

			} else {
				throw new EvaluationError("trying to div one or more things that are not ints");
			}

		} else if (b.getOperator().equals("**")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value((int) Math.pow(v1.getInt(), v2.getInt()));
			} else {
				throw new EvaluationError("trying to exp one or more things that are not ints");
			}

		} else if (b.getOperator().equals("and")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() && v2.getBool());
			} else {
				throw new EvaluationError("trying to and one or more things that are not bools");
			}
		} else if (b.getOperator().equals("or")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() || v2.getBool());
			} else {
				throw new EvaluationError("trying to or one or more things that are not bools");
			}
		} else if (b.getOperator().equals("==")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() == v2.getBool());
			} else if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() == v2.getInt());
			} else if (v1.getType().equals("string") && v2.getType().equals("string")) {
				return new Value(v1.getString().equals(v2.getString()));
			} else {
				throw new EvaluationError("trying to == two things of different or invalid types");
			}
		} else if (b.getOperator().equals("!=")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() != v2.getBool());
			} else if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() != v2.getInt());
			} else if (v1.getType().equals("string") && v2.getType().equals("string")) {
				return new Value(!(v1.getString().equals(v2.getString())));
			} else {
				throw new EvaluationError("trying to != two things of different or invalid types");
			}
		} else if (b.getOperator().equals(">")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() > v2.getInt());
			} else {
				throw new EvaluationError("trying to > one or more things that are not int");
			}
		} else if (b.getOperator().equals("<")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() < v2.getInt());
			} else {
				throw new EvaluationError("trying to < one or more things that are not int");
			}
		} else if (b.getOperator().equals(">=")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() >= v2.getInt());
			} else {
				throw new EvaluationError("trying to >= one or more things that are not int");
			}
		} else if (b.getOperator().equals("<=")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() <= v2.getInt());
			} else {
				throw new EvaluationError("trying to <= one or more things that are not int");
			}
		} else {
			throw new EvaluationError("invalid operator");
		}

	}

}

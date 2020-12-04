package interpreter;

import java.util.HashMap;
import java.util.List;

import Parser.SyntaxError;
import ast.BExpr;
import ast.Bool;
import ast.Expr;
import ast.If;
import ast.Node;
import ast.Null;
import ast.Number;
import ast.Program;
import ast.Seq;
import ast.Str;
import ast.Var;
import ast.While;

public class Interpreter {

	private HashMap<String, Value> store = null;

	public Interpreter() {
		store = new HashMap<String, Value>();
	}

	public Value evaluateProg(Program p) throws SyntaxError {
		Seq seq = (Seq) p.getChildren().get(0);
		List<Expr> children = seq.getSeq();

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

		// what value to return?

		// TEMPORARY
		// return evaluateExpr(children.get(0));

	}

	public HashMap<String, Value> getStore() {
		return store;
	}

	// evaluates a SINGLE line, not a whole program
	public Value evaluateExpr(Node n) throws SyntaxError {

		if (n instanceof Number) {

			Number r = (Number) n;
			return new Value(r.getNum());

		} else if (n instanceof Bool) {

			Bool r = (Bool) n;
			return new Value(r.getBool());

		} else if (n instanceof BExpr) { // this will include AExpr instances right?

			BExpr b = (BExpr) n;
			return evaluateBExpr(b);

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
						flag = false;
					}
					
				}else {
					throw new SyntaxError("Guard is not a boolean");
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
			throw new SyntaxError("the tree I got cannot be evaluated. Please check me.");
		}

	}

	public Value evaluateVal(Var r) throws SyntaxError {

		if (r.isValue()) {

			if (store.containsKey(r.getName())) {
				Value v = store.get(r.getName());

				if (v == null) {
					throw new SyntaxError("this variable does not have a value");
				}

				return store.get(r.getName());
			} else {
				throw new SyntaxError("this variable does not exist");
			}

		} else {
			Value v = evaluateExpr(r.getChild());
			store.put(r.getName(), v);

			return v;
		}
	}

	public Value evaluateIf(If r) throws SyntaxError {

		List<Node> guards = r.getGuards();
		List<Node> branches = r.getBranches();

		Node toExecute = null;

		int i = 0;
		while (i < guards.size()) {
			Value v = evaluateExpr(guards.get(i));
			if (!(v.getType().equals("bool"))) {
				throw new SyntaxError("a guard I tried to evaluate is not a boolean");
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

	public Value evaluateBExpr(BExpr b) throws SyntaxError {

		if (b.getOperator().equals("not")) {
			
			Value v = evaluateExpr(b.getChildren().get(1));

			if (v.getType().equals("bool")) {
				return new Value(!v.getBool());
			} else {
				throw new SyntaxError("calling NOT on something that is not a boolean");
			}

		} else if (b.getOperator().equals("+")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() + v2.getInt());
			} else {
				throw new SyntaxError("trying to add one or more things that are not ints");
			}
		} else if (b.getOperator().equals("-")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() - v2.getInt());
			} else {
				throw new SyntaxError("trying to sub one or more things that are not ints");
			}

		} else if (b.getOperator().equals("*")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() * v2.getInt());
			} else {
				throw new SyntaxError("trying to mult one or more things that are not ints");
			}

		} else if (b.getOperator().equals("/")) {

			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				if (v2.getInt() != 0) {
					return new Value(v1.getInt() / v2.getInt());
				} else {
					throw new SyntaxError("cannot divide by 0");
				}

			} else {
				throw new SyntaxError("trying to div one or more things that are not ints");
			}

		} else if (b.getOperator().equals("**")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value((int) Math.pow(v1.getInt(), v2.getInt()));
			} else {
				throw new SyntaxError("trying to exp one or more things that are not ints");
			}

		} else if (b.getOperator().equals("and")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() && v2.getBool());
			} else {
				throw new SyntaxError("trying to and one or more things that are not bools");
			}
		} else if (b.getOperator().equals("or")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("bool") && v2.getType().equals("bool")) {
				return new Value(v1.getBool() || v2.getBool());
			} else {
				throw new SyntaxError("trying to or one or more things that are not bools");
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
				throw new SyntaxError("trying to == two things of different or invalid types");
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
				throw new SyntaxError("trying to != two things of different or invalid types");
			}
		} else if (b.getOperator().equals(">")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));

			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() > v2.getInt());
			} else {
				throw new SyntaxError("trying to > one or more things that are not int");
			}
		} else if (b.getOperator().equals("<")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() < v2.getInt());
			} else {
				throw new SyntaxError("trying to < one or more things that are not int");
			}
		} else if (b.getOperator().equals(">=")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() >= v2.getInt());
			} else {
				throw new SyntaxError("trying to >= one or more things that are not int");
			}
		} else if (b.getOperator().equals("<=")) {
			Value v1 = evaluateExpr(b.getChildren().get(0));
			Value v2 = evaluateExpr(b.getChildren().get(1));
			
			if (v1.getType().equals("int") && v2.getType().equals("int")) {
				return new Value(v1.getInt() <= v2.getInt());
			} else {
				throw new SyntaxError("trying to <= one or more things that are not int");
			}
		} else {
			throw new SyntaxError("invalid operator");
		}

	}

}

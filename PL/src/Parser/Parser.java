package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import ast.*;
import ast.Expr.ExprOperator;
import ast.Number;

public class Parser{

	public Program parse(Reader r) {
		Tokenizer t = new Tokenizer(r);
		try {
			return parseProgram(t);
		} catch (SyntaxError e) {
			System.out.println(e);
			System.out.println("Syntax error: something does not follow the grammar rules.");
		}
		return null;
	}

	/**
	 * Parses a program from the stream of tokens provided by the Tokenizer,
	 * consuming tokens representing the program. All following methods with a name
	 * "parseX" have the same spec except that they parse syntactic form X.
	 *
	 * @return the created AST
	 * @throws SyntaxError if there the input tokens have invalid syntax
	 */
	public static Program parseProgram(Tokenizer t) throws SyntaxError {
//		Program p = new Program();
		
//	    Seq e = parseSeq(t);    //assuming all Exprs are AExprs
//	    p.addNode(e);
		Program p = parseProg(t);
		return p;
	  }
	
	static int paren_count = 0;
	static int brace_count = 0;
	
	public static Program parseProg(Tokenizer t) throws SyntaxError {
		Program p1 = new Program();
		while (!t.peek().getType().equals(TokenType.EOF)) {
			p1.addNode(parseFun(t));
		}
		return p1;
	}
	public static Fun parseFun(Tokenizer t) throws SyntaxError {
		Fun f1;
		if (t.peek().getType().equals(TokenType.FUN)) {
			String n = t.next().toFunToken().getValue();
			consume(t, TokenType.LPAREN, "Function args need parenthesis");
	    	ArrayList<Var> args = new ArrayList<Var>();
	    	while (t.peek().getType().equals(TokenType.VAR)) {
	    		String temp = t.next().toVarToken().getValue();
	    		Var v1 = new Var(temp, null);
				v1.setAsValue();
				args.add(v1);
				if (t.peek().getType().equals(TokenType.COMMA))
					consume(t, TokenType.COMMA);
	    	}
	    	f1 = new Fun(n, args);
	    	consume(t, TokenType.RPAREN, "Function args missing closing parenthesis");
	    	consume(t, TokenType.LBRACE, "Function body need brackets");
	    	if (t.peek().getType().equals(TokenType.RBRACE))
	    		throw new SyntaxError("Function body cannot be empty");
	    	Seq body = parseSeqCond(t);
	    	consume(t, TokenType.RBRACE, "Function body needs a closing bracket");
	    	f1.assignSeq(body);
		}
		else {
			f1 = new Fun();
			f1.assignSeq(parseSeq(t));
		}
		return f1;
	}
	public static Seq parseSeq(Tokenizer t) throws SyntaxError {
		Seq s1 = new Seq();
		s1.addToSeq(parseExpr(t));
		while (!t.peek().getType().equals(TokenType.EOF)) {
			Expr temp = parseExpr(t);
			s1.addToSeq(temp);
//			System.out.println(temp + "??");
//			System.out.println(t.peek() + "!!");
		}
		return s1;
	}
	
	public static Seq parseSeqCond(Tokenizer t) throws SyntaxError {
		Seq s1 = new Seq();
		s1.addToSeq(parseExpr(t));
		while (!t.peek().getType().equals(TokenType.EOF) && !t.peek().getType().equals(TokenType.RBRACE)) {
			s1.addToSeq(parseExpr(t));
		}
		return s1;
	}
	
	public static Expr parseExpr(Tokenizer t) throws SyntaxError {
		Expr e1;
		if (t.peek().getType().equals(TokenType.SEMICOLON)) {
			consume(t, TokenType.SEMICOLON);
			return null;
		} else if(t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        e1 = parseExpr(t);
	        consume(t, TokenType.RPAREN);
	        if (t.peek().getType().equals(TokenType.CONCAT))
	        	e1 = parseStrExpr1(t, (Type)e1);
	        else if (!t.peek().getType().equals(TokenType.NOT) && (t.peek().getType().category().equals(TC.BINOP) || t.peek().getType().category().equals(TC.BOP)))
	        	e1 = parseBExpr1(t, (Type)e1);
	    } else if (t.peek().getType().equals(TokenType.IF)) {  // If, elif, else statements
	    	consume(t, TokenType.IF);
	    	consume(t, TokenType.LPAREN, "If statement guards need parenthesis");
	    	Type g = parseBExpr(t);
	    	consume(t, TokenType.RPAREN, "If guard missing closing parenthesis");
	    	consume(t, TokenType.LBRACE, "If statements bodies need brackets");
	    	Seq body = parseSeqCond(t);
	    	consume(t, TokenType.RBRACE, "If statement body needs a closing bracket");
	    	e1 = new If(g, body);
	    	while (t.peek().getType().equals(TokenType.ELIF)) {
	    		if (t.peek().getType().equals(TokenType.ELSE))
	    			break;
	    		consume(t, TokenType.ELIF);
	    		consume(t, TokenType.LPAREN, "Else if guards need parenthesis");
		    	g = parseBExpr(t);
		    	consume(t, TokenType.RPAREN, "Else if guard missing closing parenthesis");
		    	consume(t, TokenType.LBRACE, "Else if statements bodies need brackets");
		    	Seq elifBody = parseSeqCond(t);
		    	consume(t, TokenType.RBRACE, "Else if statement bodies needs a closing bracket");
		    	((If) e1).addBranch(g, elifBody);
	    	}
	    	if (t.peek().getType().equals(TokenType.ELSE)) {
	    		consume(t, TokenType.ELSE);
	    		consume(t, TokenType.LBRACE, "Else statement bodies need brackets");
		    	Seq elseBody = parseSeqCond(t);
		    	consume(t, TokenType.RBRACE, "Else statement bodies needs a closing bracket");
	    		((If) e1).addBranch(new Bool(true), elseBody);
	    	}
		} else if (t.peek().getType().equals(TokenType.CALL)) {
			String f = t.next().toCallToken().getValue();
			e1 = new Call(f);
			consume(t, TokenType.LPAREN, "Function calls needs parenthesis");
			if (!t.peek().getType().equals(TokenType.RPAREN))
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) e1).addArg(s);
				} else {
				((Call) e1).addArg(parseBExpr(t));
				}
			while (t.peek().getType().equals(TokenType.COMMA)) {
				consume(t, TokenType.COMMA, "Function arguments should be separated by comma");
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) e1).addArg(s);
				} else {
				Type temp = parseBExpr(t);
				((Call) e1).addArg(temp);
				}
			}
			consume(t, TokenType.RPAREN, "Function calls needs closing parenthesis");
			e1 = parseBExpr1(t, (Call)e1);
	    } else if (t.peek().getType().equals(TokenType.WHILE)) {
			consume(t, TokenType.WHILE);
	    	consume(t, TokenType.LPAREN, "While statement guard needs parenthesis");
	    	e1 = new While(parseBExpr(t));
	    	consume(t, TokenType.RPAREN, "While statement guard needs closing parenthesis");
	    	consume(t,TokenType.LBRACE, "While statement needs a body using curly brackets");
	    	((While) e1).addBranch(parseSeqCond(t));
	    	consume(t,TokenType.RBRACE, "While statement body needs a closing bracket");
		} else if (t.peek().getType().equals(TokenType.VAR)) {  // Variables
			String temp = t.next().toVarToken().getValue();
			if (t.peek().getType().equals(TokenType.ASSIGN)) {
				consume(t, TokenType.ASSIGN);
				e1 = new Var(temp, parseExpr(t));
			}
			else if (t.peek().getType().equals(TokenType.SEMICOLON)) {
				e1 = new Var(temp, null);
			}
			else {
				e1 = new Var(temp, null);
				((Var) e1).setAsValue();
				e1 = parseBExpr1(t, (Var) e1);
			}
		} else if (t.peek().isBool() || t.peek().getType().equals(TokenType.NOT)) {
	    	e1 = parseBExpr(t);
	    } else if (t.peek().isNum()) {
	    	e1 = parseAExpr(t);
	    } else if (t.peek().isString() || t.peek().getType().equals(TokenType.REVERSE)) {
	    	e1 = parseStrExpr(t);
	    } else if (t.peek().isNull()) {
	    	consume(t, TokenType.NULL);
	    	e1 = new Null();
	    } else{
	    	System.out.println("Parse error on char \'" + t.peek() + "\'");
	    	throw new SyntaxError("Parsing Expression failed on line " + t.lineNumber());
		};
		return e1;
	}
	
	public static Type parseStrExpr(Tokenizer t) throws SyntaxError {
		Type s1;
		if (t.peek().getType().equals(TokenType.LPAREN)) {
			consume(t, TokenType.LPAREN);
	        s1 = parseStrExpr(t);
	        consume(t, TokenType.RPAREN);
		} else if (t.peek().getType().equals(TokenType.REVERSE)) {
			consume(t, TokenType.REVERSE);
			s1 = new StrExpr(parseStrExpr(t));
		} else if (t.peek().isString()) {
			s1 = new Str(t.next().toStringToken().getValue());
		} else {
			throw new SyntaxError("Assigning String failed on line " + t.lineNumber());
		}
		System.out.println(s1 + "\n" + t.peek());
		if (t.peek().getType().equals(TokenType.CONCAT)) {
			consume(t, TokenType.CONCAT);
			s1 = new StrExpr(s1, ExprOperator.CONCAT, parseStrExpr(t));
		}
		return s1;
	}
	
	public static Type parseStrExpr1(Tokenizer t, Type temp) throws SyntaxError {
		Type s1 = temp;
		if (t.peek().getType().equals(TokenType.CONCAT)) {
			consume(t, TokenType.CONCAT);
			s1 = new StrExpr(s1, ExprOperator.CONCAT, parseStrExpr(t));
		}
		return s1;
	}
	
	public static Type parseBExpr(Tokenizer t) throws SyntaxError{
		Type b1;
	    if (t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        b1 = parseBExpr(t);
	        consume(t, TokenType.RPAREN);
	    } else if (t.peek().getType().equals(TokenType.NOT)){
			consume(t, TokenType.NOT);
			Type btemp = parseBExpr(t);
			if (btemp.nodeType().equals("num"))
				throw new SyntaxError("Cannot assign not to a number on line " + t.lineNumber());
			b1 = new BExpr(btemp);
	    } else if (t.peek().isBool()) {
	    	boolean value = t.peek().getType() == TokenType.TRUE;
			b1 = new Bool(value);
			if (value) 
				consume(t, TokenType.TRUE);
			else 
				consume(t, TokenType.FALSE);
	    } else if (t.peek().isNull()) {
	    	consume(t, TokenType.NULL);
	    	b1 = new Null();
	    } else if (t.peek().isNum()) {
	    	b1 = parseAExpr(t);
	    } else if (t.peek().isVar()) {
			b1 = new Var(t.next().toVarToken().getValue(), null);
			((Var) b1).setAsValue();
			b1 = parseAExpr(t, b1);
	    } else if (t.peek().getType().equals(TokenType.CALL)) {
			String f = t.next().toCallToken().getValue();
			b1 = new Call(f);
			consume(t, TokenType.LPAREN, "Function calls needs parenthesis");
			if (!t.peek().getType().equals(TokenType.RPAREN))
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) b1).addArg(s);
				} else {
				((Call) b1).addArg(parseBExpr(t));
				}
			while (t.peek().getType().equals(TokenType.COMMA)) {
				consume(t, TokenType.COMMA, "Function arguments should be separated by comma");
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) b1).addArg(s);
				} else {
				Type temp = parseBExpr(t);
				((Call) b1).addArg(temp);
				}
			}
			consume(t, TokenType.RPAREN, "Function calls needs closing parenthesis");
	    } else{
	    	System.out.println(t.peek().getType());
	    	throw new SyntaxError("Assigning Boolean Values failed on line " + t.lineNumber());
		};
		
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch");
		} else if (t.peek().getType().equals(TokenType.AND)) {
			consume(t, TokenType.AND);
			errOnNumber(t,b1);
			b1 = new BExpr(b1, ExprOperator.AND, errOnNumber(t, parseBExpr(t)));
		} else if (t.peek().getType().equals(TokenType.OR)){
			consume(t, TokenType.OR);
			errOnNumber(t,b1);
			b1 = new BExpr(b1, ExprOperator.OR, errOnNumber(t, parseBExpr(t)));
		} else if (t.peek().getType().equals(TokenType.EQ)){
			consume(t, TokenType.EQ);
			b1 = new BExpr(b1, ExprOperator.EQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.NEQ)){
			consume(t, TokenType.NEQ);
			b1 = new BExpr(b1, ExprOperator.NEQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.GT)){
			consume(t, TokenType.GT);
			b1 = new BExpr(b1, ExprOperator.GT, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.LT)){
			consume(t, TokenType.LT);
			b1 = new BExpr(b1, ExprOperator.LT, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.GTE)){
			consume(t, TokenType.GTE);
			b1 = new BExpr(b1, ExprOperator.GTE, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.LTE)){
			consume(t, TokenType.LTE);
			b1 = new BExpr(b1, ExprOperator.LTE, parseBExpr(t));
		}
	    return b1;
	  }
	
	public static Type parseBExpr1(Tokenizer t, Type btemp) throws SyntaxError{
		Type b1 = btemp;
		
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch");
		} else if (t.peek().getType().equals(TokenType.AND)) {
			consume(t, TokenType.AND);
			errOnNumber(t,b1);
			b1 = new BExpr(b1, ExprOperator.AND, errOnNumber(t, parseBExpr(t)));
		} else if (t.peek().getType().equals(TokenType.OR)){
			consume(t, TokenType.OR);
			errOnNumber(t,b1);
			b1 = new BExpr(b1, ExprOperator.OR, errOnNumber(t, parseBExpr(t)));
		} else if (t.peek().getType().equals(TokenType.EQ)){
			consume(t, TokenType.EQ);
			b1 = new BExpr(b1, ExprOperator.EQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.NEQ)){
			consume(t, TokenType.NEQ);
			b1 = new BExpr(b1, ExprOperator.NEQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.GT)){
			consume(t, TokenType.GT);
			b1 = new BExpr(b1, ExprOperator.GT, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.LT)){
			consume(t, TokenType.LT);
			b1 = new BExpr(b1, ExprOperator.LT, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.GTE)){
			consume(t, TokenType.GTE);
			b1 = new BExpr(b1, ExprOperator.GTE, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.LTE)){
			consume(t, TokenType.LTE);
			b1 = new BExpr(b1, ExprOperator.LTE, parseBExpr(t));
		}
	    return parseAExpr(t, b1);
	  }
	
	// Throws error if b is a NUM, otherwise does nothing
	private static Type errOnNumber(Tokenizer t, Type b1) throws SyntaxError {
		if (b1.nodeType().equals("num"))
			throw new SyntaxError("Failed assigning booleans on logical operators (perhaps add parenthesis) on line " + t.lineNumber());
		return b1;
	}
	
	public static Type parseAExpr(Tokenizer t) throws SyntaxError{
		Type a1 = parseAExprVal(t);
		
		
		if (t.peek().getType().equals(TokenType.TIMES)){
			consume(t, TokenType.TIMES);
			a1 = new AExpr(a1, ExprOperator.TIMES, parseAExprVal(t));
		} else if (t.peek().getType().equals(TokenType.DIVIDE)){
			consume(t, TokenType.DIVIDE);
			a1 = new AExpr(a1, ExprOperator.DIVIDE, parseAExprVal(t));
		}
		
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch on line " + t.lineNumber());
		}
		else if (t.peek().getType().equals(TokenType.PLUS)) {
				consume(t, TokenType.PLUS);
				a1 = new AExpr(a1, ExprOperator.PLUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.MINUS)){
				consume(t, TokenType.MINUS);
				a1 = new AExpr(a1, ExprOperator.MINUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.EXP)){
		  consume(t, TokenType.EXP);
				a1 = new AExpr(a1, ExprOperator.EXP, parseAExpr(t));
		} 
	    return a1;
	  }
	
	public static Type parseAExpr(Tokenizer t, Type b) throws SyntaxError{
		Type a1 = b;
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch on line " + t.lineNumber());
		}
		else if (t.peek().getType().equals(TokenType.PLUS)) {
				consume(t, TokenType.PLUS);
				a1 = new AExpr(a1, ExprOperator.PLUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.MINUS)){
				consume(t, TokenType.MINUS);
				a1 = new AExpr(a1, ExprOperator.MINUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.EXP)){
		  consume(t, TokenType.EXP);
				a1 = new AExpr(a1, ExprOperator.EXP, parseAExpr(t));
		} 
	    return a1;
	  }
	
	public static Type parseAExprVal(Tokenizer t) throws SyntaxError{
		Type a1;
		if(t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        paren_count++;
	        a1 = parseAExpr(t);
	        paren_count--;
	        consume(t, TokenType.RPAREN);
	    } else if (t.peek().isNum()) {
				long value = t.peek().toNumToken().getValue();
				a1 = new Number(value);
	      consume(t, TokenType.NUM);
	    } else if (t.peek().isVar()) {   
			a1 = new Var(t.next().toVarToken().getValue(), null);
			((Var) a1).setAsValue();
	    } else if (t.peek().getType().equals(TokenType.CALL)) {
			String f = t.next().toCallToken().getValue();
			a1 = new Call(f);
			consume(t, TokenType.LPAREN, "Function calls needs parenthesis");
			if (!t.peek().getType().equals(TokenType.RPAREN))
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) a1).addArg(s);
				} else {
				((Call) a1).addArg(parseBExpr(t));
				}
			while (t.peek().getType().equals(TokenType.COMMA)) {
				consume(t, TokenType.COMMA, "Function arguments should be separated by comma");
				if (t.peek().getType().equals(TokenType.STRING)) {
					Str s = new Str(t.next().toStringToken().getValue());
					((Call) a1).addArg(s);
				} else {
				Type temp = parseBExpr(t);
				((Call) a1).addArg(temp);
				}
			}
			consume(t, TokenType.RPAREN, "Function calls needs closing parenthesis");
	    } else{
	      throw new SyntaxError("Assigning Arithmetic Values failed on line " + t.lineNumber());
		};
		
		if (t.peek().getType().equals(TokenType.TIMES)){
			consume(t, TokenType.TIMES);
			a1 = new AExpr(a1, ExprOperator.TIMES, parseAExprVal(t));
		} else if (t.peek().getType().equals(TokenType.DIVIDE)){
			consume(t, TokenType.DIVIDE);
			a1 = new AExpr(a1, ExprOperator.DIVIDE, parseAExprVal(t));
		}
		return a1;
	}

	/**
	 * Consumes a token of the expected type.
	 *
	 * @throws SyntaxError if the wrong kind of token is encountered.
	 */
	public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {
		if (tt.equals(TokenType.LPAREN))
        	paren_count++;
		else if (tt.equals(TokenType.RPAREN))
			paren_count--;
		else if (tt.equals(TokenType.LBRACE))
			brace_count++;
		else if (tt.equals(TokenType.RBRACE))
			brace_count--;
		if (paren_count < 0)
			throw new SyntaxError("Parenthesis mismatch at line " + t.lineNumber());
		if (brace_count < 0)
			throw new SyntaxError("Bracket mismatch at line " + t.lineNumber());
			
			
		if (t.peek().getType().equals(tt)) {
			t.next();
		} else
			throw new SyntaxError("Syntax error on consuming "+ t.peek().getType() + " Expected " + tt.name());
	}
	
	public static void consume(Tokenizer t, TokenType tt, String err) throws SyntaxError {
		if (t.peek().getType().equals(TokenType.ERROR))
			System.out.println(t.peek());
		if (tt.equals(TokenType.LPAREN))
        	paren_count++;
		else if (tt.equals(TokenType.RPAREN))
			paren_count--;
		else if (tt.equals(TokenType.LBRACE))
			brace_count++;
		else if (tt.equals(TokenType.RBRACE))
			brace_count--;
		if (paren_count < 0)
			throw new SyntaxError("Parenthesis mismatch at line " + t.lineNumber());
		if (brace_count < 0)
			throw new SyntaxError("Bracket mismatch at line " + t.lineNumber());
			
			
		if (t.peek().getType().equals(tt)) {
			t.next();
		} else
			throw new SyntaxError(err);
	}
}

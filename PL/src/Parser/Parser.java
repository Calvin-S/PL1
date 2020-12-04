package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
		Program p = new Program();
		
	    Seq e = parseSeq(t);    //assuming all Exprs are AExprs
	    p.addNode(e);
  
		return p;
	  }
	
	static int paren_count = 0;
	static int brace_count = 0;
	
	public static Seq parseSeq(Tokenizer t) throws SyntaxError {
		Seq s1 = new Seq();
		s1.addToSeq(parseExpr(t));
		while (!t.peek().getType().equals(TokenType.EOF)) {
			s1.addToSeq(parseExpr(t));
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
	    } else if (t.peek().getType().equals(TokenType.IF)) {  // If, elif, else statements
	    	consume(t, TokenType.IF);
	    	consume(t, TokenType.LPAREN, "If statement guards need parenthesis");
	    	Type g = parseBExpr(t);
	    	consume(t, TokenType.RPAREN);
	    	consume(t, TokenType.LBRACE, "If statements bodies need brackets");
	    	Seq body = parseSeqCond(t);
	    	consume(t, TokenType.RBRACE);
	    	e1 = new If(g, body);
	    	while (t.peek().getType().equals(TokenType.ELIF)) {
	    		if (t.peek().getType().equals(TokenType.ELSE))
	    			break;
	    		consume(t, TokenType.ELIF, "Else if statements guards need parenthesis");
	    		consume(t, TokenType.LPAREN);
		    	g = parseBExpr(t);
		    	consume(t, TokenType.RPAREN);
		    	consume(t, TokenType.LBRACE, "Else if statements bodies need brackets");
		    	Seq elifBody = parseSeqCond(t);
		    	consume(t, TokenType.RBRACE);
		    	((If) e1).addBranch(g, elifBody);
	    	}
	    	if (t.peek().getType().equals(TokenType.ELSE)) {
	    		consume(t, TokenType.ELSE);
	    		consume(t, TokenType.LBRACE, "Else statement bodies need brackets");
		    	Seq elseBody = parseSeqCond(t);
		    	consume(t, TokenType.RBRACE);
	    		((If) e1).addBranch(new Bool(true), elseBody);
	    	}
		} else if (t.peek().getType().equals(TokenType.WHILE)) {
			consume(t, TokenType.WHILE);
	    	consume(t, TokenType.LPAREN, "While statement guard needs parenthesis");
	    	e1 = new While(parseBExpr(t));
	    	consume(t, TokenType.RPAREN);
	    	consume(t,TokenType.LBRACE, "While statement needs a body using curly brackets");
	    	((While) e1).addBranch(parseSeqCond(t));
	    	consume(t,TokenType.RBRACE, "While statement body needs closing brackets");
		} else if (t.peek().getType().equals(TokenType.VAR)) {  // Variables
			String temp = t.next().toVarToken().getValue();
			if (t.peek().getType().equals(TokenType.ASSIGN)) {
				consume(t, TokenType.ASSIGN);
				e1 = new Var(temp, parseExpr(t));
			}
			else {
				e1 = new Var(temp, null);
			}
		} else if (t.peek().isBool() || t.peek().getType().equals(TokenType.NOT)) {
	    	e1 = parseBExpr(t);
	    } else if (t.peek().isNum()) {
	    	e1 = parseAExpr(t);
	    } else if (t.peek().isString()) {
	    	String temp = t.next().toStringToken().getValue();
	    	e1 = new Str(temp);
	    } else if (t.peek().isNull()) {
	    	consume(t, TokenType.NULL);
	    	e1 = new Null();
	    } else{
	    	System.out.println(t.peek());
	    	throw new SyntaxError("Assigning Boolean Values failed on line " + t.lineNumber());
		};
		return e1;
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
	    } else if (t.peek().isNum()) {
	    	b1 = parseAExpr(t);
	    } else if (t.peek().isVar()) {   
			b1 = new Var(t.next().toVarToken().getValue(), null);
			((Var) b1).setAsValue();
			b1 = parseAExpr(t, b1);
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
	    } else{
	      throw new SyntaxError("Assigning Arithmetic Values failed on line " + t.lineNumber());
		};
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

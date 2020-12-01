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
		
	    Expr e = parseExpr(t);    //assuming all Exprs are AExprs
	    p.addNode(e);
  
		return p;
	  }
	
	static int paren_count = 0;
	static int brace_count = 0;
	
	public static Expr parseExpr(Tokenizer t) throws SyntaxError {
		Expr e1;
		if(t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        e1 = parseExpr(t);
	        consume(t, TokenType.RPAREN);
	    } else if (t.peek().getType().equals(TokenType.IF)) {
	    	consume(t, TokenType.IF);
	    	consume(t, TokenType.LPAREN, "If statement guards need parenthesis");
	    	BExpr g = parseBExpr(t);
	    	consume(t, TokenType.RPAREN);
	    	consume(t, TokenType.LBRACE, "If statements bodies need brackets");
	    	Expr body = parseExpr(t);
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
		    	Expr elifBody = parseExpr(t);
		    	consume(t, TokenType.RBRACE);
		    	((If) e1).addBranch(g, elifBody);
	    	}
	    	if (t.peek().getType().equals(TokenType.ELSE)) {
	    		consume(t, TokenType.ELSE);
	    		consume(t, TokenType.LBRACE, "Else statement bodies need brackets");
		    	Expr elseBody = parseExpr(t);
		    	consume(t, TokenType.RBRACE);
	    		((If) e1).addBranch(new Bool(true), elseBody);
	    	}
		} else if (t.peek().isBool() || t.peek().getType().equals(TokenType.NOT)) {
	    	e1 = parseBExpr(t);
	    } else if (t.peek().isNum()) {
	    	e1 = parseAExpr(t);
	    } else{
	    	throw new SyntaxError("Assigning Boolean Values failed on line " + t.lineNumber());
		};
		return e1;
	}
	
	public static BExpr parseBExpr(Tokenizer t) throws SyntaxError{
		BExpr b1;
	    if (t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        b1 = parseBExpr(t);
	        consume(t, TokenType.RPAREN);
	    } else if (t.peek().getType().equals(TokenType.NOT)){
			consume(t, TokenType.NOT);
			BExpr btemp = parseBExpr(t);
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
	    } else{
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
//		else if (t.hasNext()){
//		  throw new SyntaxError("Boolean binop failed on line " + t.lineNumber());
//		} 
		
	    return b1;
	  }
	
	// Throws error if b is a NUM, otherwise does nothing
	private static BExpr errOnNumber(Tokenizer t, BExpr b) throws SyntaxError {
		if (b.nodeType().equals("num"))
			throw new SyntaxError("Cannot assign not to a number on line " + t.lineNumber());
		return b;
	}
	
	public static AExpr parseAExpr(Tokenizer t) throws SyntaxError{
		AExpr a1 = parseAExprVal(t);
		
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
	
	public static AExpr parseAExprVal(Tokenizer t) throws SyntaxError{
		AExpr a1;
		if(t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        paren_count++;
	        a1 = parseAExpr(t);
	        paren_count--;
	        consume(t, TokenType.RPAREN);
	    }else if (t.peek().isNum()) {
				int value = t.peek().toNumToken().getValue();
				a1 = new Number(value);
	      consume(t, TokenType.NUM);
	    }else{
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
			throw new SyntaxError("Syntax error");
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

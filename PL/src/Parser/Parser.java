package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import ast.*;
import ast.Expr.ExprOperator;
import ast.Number;

class Parser{

	public static void main(String[] args) {
		InputStream in = ClassLoader.getSystemResourceAsStream("Examples/test.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		
		Parser parser = new Parser();
		Program prog = parser.parse(r);
		System.out.println(prog);
	}

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
		
	    Expr e = parseBExpr(t);    //assuming all Exprs are AExprs
	    p.addNode(e);
  
		return p;
	  }
	
	static int paren_count = 0;
	public static BExpr parseBExpr(Tokenizer t) throws SyntaxError{
		BExpr b1;
	    if(t.peek().getType().equals(TokenType.LPAREN)){
	        consume(t, TokenType.LPAREN);
	        paren_count++;
	        b1 = parseBExpr(t);
	        paren_count--;
	        consume(t, TokenType.RPAREN);
	    }else if (t.peek().isBool()) {
	    	boolean value = t.peek().getType() == TokenType.TRUE;
			b1 = new Bool(value);
			if (value) 
				consume(t, TokenType.TRUE);
			else 
				consume(t, TokenType.FALSE);
	    }else{
	      throw new SyntaxError("Boolean Paren or Values fail");
		};
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch");
		}
		else if (t.peek().getType().equals(TokenType.AND)) {
			consume(t, TokenType.AND);
			b1 = new BExpr(b1, ExprOperator.AND, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.OR)){
			consume(t, TokenType.OR);
			b1 = new BExpr(b1, ExprOperator.OR, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.EQ)){
			consume(t, TokenType.EQ);
			b1 = new BExpr(b1, ExprOperator.EQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.NEQ)){
			consume(t, TokenType.NEQ);
			b1 = new BExpr(b1, ExprOperator.NEQ, parseBExpr(t));
		} else if (t.peek().getType().equals(TokenType.NOT)){
			consume(t, TokenType.NOT);
			b1 = new BExpr(parseBExpr(t));
		} else if (t.hasNext()){
		  throw new SyntaxError("Boolean binop fails");
		    }
		
	    return b1;
	  }
	
	public static AExpr parseAExpr(Tokenizer t) throws SyntaxError{
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
	      throw new SyntaxError("Arith Paren or Values fail");
		};
		
		if (t.peek().getType().equals(TokenType.RPAREN)) {
			if (paren_count <= 0)
				throw new SyntaxError("Parenthesis Mismatch");
		}
		else if (t.peek().getType().equals(TokenType.PLUS)) {
				consume(t, TokenType.PLUS);
				a1 = new AExpr(a1, ExprOperator.PLUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.MINUS)){
				consume(t, TokenType.MINUS);
				a1 = new AExpr(a1, ExprOperator.MINUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.TIMES)){
		  consume(t, TokenType.TIMES);
				a1 = new AExpr(a1, ExprOperator.TIMES, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.DIVIDE)){
		  consume(t, TokenType.DIVIDE);
				a1 = new AExpr(a1, ExprOperator.DIVIDE, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.EXP)){
		  consume(t, TokenType.EXP);
				a1 = new AExpr(a1, ExprOperator.EXP, parseAExpr(t));
		} else if (t.hasNext()){
		  throw new SyntaxError("Arith Binop fails");
		    }
		
	    return a1;
	  }

	/**
	 * Consumes a token of the expected type.
	 *
	 * @throws SyntaxError if the wrong kind of token is encountered.
	 */
	public static void consume(Tokenizer t, TokenType tt) throws SyntaxError {

		if (t.peek().getType().equals(tt)) {
			t.next();
		} else
			throw new SyntaxError("Syntax error");
	}
}

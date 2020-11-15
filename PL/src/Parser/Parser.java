package Parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import ast.*;

class Parser{

	public static void main(String[] args) {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/example-rules.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = new Parser();
		Program prog = parser.parse(r);
	}

	@Override
	public Program parse(Reader r) {
		Tokenizer t = new Tokenizer(r);
		try {
			return parseProgram(t);
		} catch (SyntaxError e) {
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

    Expr e = parseAExpr(t);    //assuming all Exprs are AExprs
    p.addNode(e);
    
		return p;
  }
  
  public static AExpr parseAExpr(Tokenizer t) throws SyntaxError{

    AExpr a1;
    if(t.peek().getType().equals(TokenType.LPAREN)){
        consume(t, TokenType.LPAREN);
        a1 = parseAExpr(t);
        consume(t, TokenType.RPAREN);
    }else if (t.peek().isNum()) {
			int value = t.peek().toNumToken().getValue();
			a1 = new Number(value);
      consume(t, TokenType.NUM);
    }else{
      throw new SyntaxError();
    };

		if (t.peek().getType().equals(TokenType.PLUS)) {
				consume(t, TokenType.PLUS);
				a1 = new AExpr(a1, ExprOperator.PLUS, parseAExpr(t));
		} else if (t.peek().getType().equals(TokenType.MINUS)){
				consume(t, TokenType.MINUS);
				a1 = new AExpr(a1, ExprOperator.MINUS, parseAExpr(t));
    }else if (t.peek().getType().equals(TokenType.TIMES)){
      consume(t, TokenType.TIMES);
			a1 = new AExpr(a1, ExprOperator.TIMES, parseAExpr(t));
    }else if (t.peek().getType().equals(TokenType.DIVIDE)){
      consume(t, TokenType.DIVIDE);
			a1 = new AExpr(a1, ExprOperator.DIVIDE, parseAExpr(t));
    }else if (t.peek().getType().equals(TokenType.EXP)){
      consume(t, TokenType.EXP);
			a1 = new AExpr(a1, ExprOperator.EXP, parseAExpr(t));
    }else if (t.hasNext()){
      throw new SyntaxError();
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

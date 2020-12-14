package Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A Tokenizer turns a Reader into a stream of tokens that can be iterated over
 * using a {@code for} loop.
 */
public class Tokenizer implements Iterator<Token> {

	/**
	 * Lookahead buffer. Contains characters already read from the {@code Reader}
	 * but not processed into a token yet.
	 */
	private final LookAheadBuffer in;

	/**
	 * The line number of the input file. Always equals 1 + the number of new line
	 * characters previously encountered.
	 */
	private int lineNumber;

	/** Queue of tokens produced by this Tokenizer but not yet read. */
	private final Queue<Token> tokens = new LinkedList<>();

	/** StringBuilder used to scan keywords and numbers. */
	private final StringBuilder sb = new StringBuilder();

	/**
	 * Create a Tokenizer that reads from the specified reader
	 *
	 * @param r The source from which the Tokenizer lexes input into Tokens
	 */
	public Tokenizer(Reader r) {
		in = new LookAheadBuffer(4, new BufferedReader(r));
		lineNumber = 1;
	}

	/**
	 * Returns {@code true} if there are more meaningful tokens to be read. In other
	 * words, returns {@code true} if {@link #next} would return a non-EOF token.
	 *
	 * @return {@code true} if there are more meaningful tokens
	 * @throws TokenizerIOException if an IOException was thrown while trying to
	 *                              read from the source Reader
	 */
	@Override
	public boolean hasNext() {
		return !(peek() instanceof Token.EOFToken);
	}

	/**
	 * Returns the next unread token. If the input is exhausted, returns the EOF
	 * token.
	 *
	 * @return the next unread token or EOF
	 * @throws TokenizerIOException if an IOException was thrown while trying to
	 *                              read from the source Reader
	 */
	@Override
	public Token next() {
		peek();
		return tokens.poll();
	}

	/**
	 * Returns the next available token without consuming the token. If there are no
	 * more tokens available, returns the EOF token.
	 *
	 * @return the next token without consuming it
	 * @throws TokenizerIOException if an IOException was thrown while trying to
	 *                              read from the source Reader
	 */
	public Token peek() {
		if (tokens.isEmpty()) {
			try {
				lexOneToken();
			} catch (IOException e) {
				System.out.println("ih");
				throw new TokenizerIOException(e);
			}
		}
		return tokens.peek();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	int lineNumber() {
		return lineNumber;
	}

	/**
	 * Constructs one token from the input source and pushes it onto the token
	 * queue. A token is always produced, but it may be an error token or an
	 * end-of-file (EOF) token.
	 *
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void lexOneToken() throws IOException {
		char c = in.next();
		
		// consume whitespace
		while (Character.isWhitespace(c)) {
			if (c == '\n')
				lineNumber++;
			c = in.next();
		}
		switch (c) {
		case LookAheadBuffer.EOF:
			addEOFToken();
			break;
		case ';':
			addToken(TokenType.SEMICOLON);
			break;
		case '[':
			addToken(TokenType.LBRACKET);
			break;
		case ']':
			addToken(TokenType.RBRACKET);
			break;
		case '(':
			addToken(TokenType.LPAREN);
			break;
		case ')':
			addToken(TokenType.RPAREN);
			break;
		case '{':
			addToken(TokenType.LBRACE);
			break;
		case '}':
			addToken(TokenType.RBRACE);
			break;
		case '=':
			lexEq();
			break;
		case '+':
			addToken(TokenType.PLUS);
			break;
		case '-':
			lexMinus();
			break;
		case '*':
			addToken(TokenType.TIMES);
			break;
		case '/':
			lexDiv();
			break;
		case '<':
			lexLAngle();
			break;
		case '>':
			lexRAngle();
			break;
		case '!':
			consume('=', TokenType.NEQ);
			break;
		case '"':
			lexStr();
			break;
		case 'T':
			addToken(TokenType.TRUE);
			break;
		case 'F':
			addToken(TokenType.FALSE);
			break;
		case 'a':
			consume("nd", "Expected and", TokenType.AND);
			break;
		case 'e':
			lexE();
			break;
		case 'f':
			lexf();
			break;
		case 'i':
			consume('f', TokenType.IF);
			break;
		case 'n':
			lexN();
			break;
		case 'o':
			consume('r', TokenType.OR);
			break;
		case 'v':
			lexVar(false);
			break;
		case 'w':
			lexWhile();
			break;
		case '$':
			lexVar(true);
			break;
		case '@':
			lexCall();
			break;
		case ',':
			addToken(TokenType.COMMA);
			break;
		case '~':
			addToken(TokenType.REVERSE);
			break;
		case '^':
			addToken(TokenType.CONCAT);
			break;
		default:
			if (Character.isDigit(c))
				lexNum(c, true);
			else
				addErrorToken(String.format("Unrecognized character %c", c));
		}
	}
	
	private void consume(String next, String error, TokenType peeked) throws IOException {
		int index = 0;
		boolean matched = false;
		while (index < next.length()) {
			if ( next.charAt(index) != in.peek(index++)) {
				addErrorToken(String.format(error));
				return;
			}
		}
		for (int i = 0; i < next.length() - 1; i++)
			in.next();
		consume(next.charAt(next.length() - 1),peeked);
	}
	
	private void consume(String next, String error) throws IOException {
		int index = 0;
		boolean matched = false;
		while (index < next.length()) {
			if ( next.charAt(index++) != in.peek()) {
				addErrorToken(String.format(error));
				return;
			}
			in.next();
		}
	}
	
	private void lexEq() throws IOException {
		if (in.peek() == '=') {
			consume('=', TokenType.EQ);
		} else {
			addToken(TokenType.ASSIGN);
		}
	}
	
	private void lexf() throws IOException {
		consume("un ", "Expected fun");
		String n = "";
		while (!Character.isWhitespace(in.peek()) && in.peek() != '(') {
			n += Character.toString(in.next());
		}
		if (n.equals(""))
			addErrorToken("Function has no name on line number " + lineNumber);
		tokens.add(new Token.FunToken(n, lineNumber));
	}
	
	private void lexVar(boolean usingDollar) throws IOException {
		if (!usingDollar)
			consume("ar ", "Expected 'var [name]'");
		String n = "";
		while (!Character.isWhitespace(in.peek()) && in.peek() != LookAheadBuffer.EOF && in.peek() != ';' && in.peek() != ')' && in.peek() != '}' && in.peek() != ',') {
			n += Character.toString(in.next());
		}
		tokens.add(new Token.VarToken(n, lineNumber));
	}
	
	private void lexCall() throws IOException {
		String n = "";
		while (!Character.isWhitespace(in.peek()) && in.peek() != '(') {
			n += Character.toString(in.next());
		}
		tokens.add(new Token.CallToken(n, lineNumber));
	}
	
	private void lexMinus() throws IOException {
		if (Character.isDigit(in.peek()))
			lexNum(in.next(), false);
		else
			addToken(TokenType.MINUS);
	}
	
	private void lexWhile() throws IOException {
		consume("hile", "Expected while");
		addToken(TokenType.WHILE);
	}
	
	private void lexStr() throws IOException {
		String n = "";
		while (in.peek() != '"'&& in.peek() != LookAheadBuffer.EOF) {
			n += Character.toString(in.next());
		}
		if (in.peek() == '"')
			in.next();
		tokens.add(new Token.StringToken(n, lineNumber));
	}
	
	private void lexN() throws IOException {
//		consume("ot", "Expected not", TokenType.NOT);
		
		if (in.peek() == 'o')
			consume("ot", "Expected not", TokenType.NOT);
		else {
			consume("ull", "Expected null", TokenType.NULL);
		}
	}
	
	/**
	 * Lexes a division symbol '/'. May be the start of an end-of-line comment with
	 * //, in which case the comment is ignored.
	 *
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void lexDiv() throws IOException {
		if (in.peek() == '/') { // comment - scan to end of line or EOF
			char c = in.scanAndPeek();
			while (c != LookAheadBuffer.EOF && c != '\n') {
				c = in.scanAndPeek();
			}
			lexOneToken();
		} else { // division
			addToken(TokenType.DIVIDE);
		}
	}

	/**
	 * Lexes '<'. May be called only when the previously read character is '<'. May
	 * be either '<' or '<='.
	 *
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void lexLAngle() throws IOException {
		if (in.peek() == '=') {
			in.next();
			addToken(TokenType.LTE);
		} else {
			addToken(TokenType.LT);
		}
	}
	
	private void lexE() throws IOException {
		if (in.peek() == 'l') {
			in.next();
			if (in.peek() == 'i')
				consume("if", "Expected elif", TokenType.ELIF);
			else if (in.peek() == 's')
				consume("se", "Expected else", TokenType.ELSE);
			else
				addErrorToken(String.format("Expected elif or else"));
		}
	}

	/**
	 * Lexes '>'. May be called only when the previously read character is '>'. May
	 * be either '>' or '>='.
	 *
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void lexRAngle() throws IOException {
		if (in.peek() == '=') {
			in.next();
			addToken(TokenType.GTE);
		} else {
			addToken(TokenType.GT);
		}
	}

	/**
	 * Lexes a number. May be called only when the previously read character is a
	 * digit. Scans the number and produces a number token.
	 *
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void lexNum(char c, boolean isPos) throws IOException {
		sb.setLength(0);
		sb.append(c);
		c = in.peek();
		while (Character.isDigit(c)) {
			sb.append(c);
			c = in.scanAndPeek();
		}
		
		try {
			long val = Long.parseLong(sb.toString());
			val = isPos ? val : -val;
			tokens.add(new Token.NumToken(val, lineNumber));
			
		} catch (NumberFormatException e) {
			addErrorToken(String.format("Number expected, got %s", sb.toString()));
		}
	}

	/**
	 * Pushes a token of the given type.
	 *
	 * @param tokenType the type of the token to pushed, not {@code null}
	 */
	private void addToken(TokenType tokenType) {
//		System.out.println(tokenType);
		tokens.add(new Token(tokenType, lineNumber));
	}

	/**
	 * Read the next character and push a token of the given type if it is the
	 * expected character. If not, push an error token.
	 *
	 * @param expected The expected next character
	 * @param tt       The {@code TokenType} to push on success
	 * @throws IOException if an IOException was thrown when trying to read from the
	 *                     source Reader
	 */
	private void consume(char expected, TokenType tt) throws IOException {
		char c = in.next();
		if (c == expected)
			addToken(tt);
		else
			addErrorToken(String.format("Expected %c, got %c", expected, c));
	}

	/** Pushes an error token with the given message. */
	private void addErrorToken(String message) {
		tokens.add(new Token.ErrorToken(message, lineNumber));
	}

	/** Pushes an and-of-file token. */
	private void addEOFToken() {
		tokens.add(new Token.EOFToken("EOF", lineNumber));
	}

	/**
	 * Helper exception to indicate an IO exception while tokenizing. This is a
	 * RuntimeException, which means it does not have to be declared in the method
	 * header.
	 */
	static class TokenizerIOException extends RuntimeException {
		TokenizerIOException(Throwable cause) {
			super(cause);
		}
	}
}
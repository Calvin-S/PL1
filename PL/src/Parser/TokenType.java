package Parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** An instance represents a Token with a category and a string representation. */
public enum TokenType {
    PLUS(TC.BINOP, "+"),
    MINUS(TC.BINOP, "-"),
	TIMES(TC.BINOP, "*"),
	DIVIDE(TC.BINOP, "/"),
	
	LT(TC.BOP, "<"),
	GT(TC.BOP, ">"),
	LTE(TC.BOP,"<="),
	GTE(TC.BOP, ">="),
	EQ(TC.BOP, "=="),
	NEQ(TC.BOP, "!="),
	AND(TC.BOP, "and"),
	OR(TC.BOP, "or"),
	NOT(TC.BOP, "not"),
	
	REVERSE(TC.STRING, "rev"),
	CONCAT(TC.STRING, "^"),
	LEN(TC.STRING, "len"),
	
	STRING(TC.VALUES, "<str>"),
	NUM(TC.VALUES, "<number>"),
    TRUE(TC.VALUES, "t"),
    FALSE(TC.VALUES, "f"),
    VAR(TC.VALUES, "<var>"),
    NULL(TC.VALUES, "null"),
    
    INT(TC.TYPES, "int"),
    STR(TC.TYPES, "string"),
    BOOL(TC.TYPES, "bool"),
    LIST(TC.TYPES, "list"),
    
    IF(TC.COND, "if"),
    ELIF(TC.COND, "elif"),
    ELSE(TC.COND, "else"),
    FOR(TC.COND, "for"),
    WHILE(TC.COND, "while"),
    MATCH(TC.COND, "match"),
    
    FUN(TC.FUNC, "fun"),
    CALL(TC.FUNC, "call"),
    
    LBRACKET(TC.OTHER, "["),
    RBRACKET(TC.OTHER, "]"),
    INSERT(TC.LIST, "insert"),
    REMOVE(TC.LIST, "remove"),
    GET(TC.LIST, "get"),
    REPLACE(TC.LIST, "replace"),
    SIZE(TC.LIST, "size"),
    
    ASSIGN(TC.OTHER, "="),
    PERIOD(TC.OTHER, "."),
	SEMICOLON(TC.OTHER, ";"),
	COLON(TC.OTHER, ":"),
	COMMA(TC.OTHER, ","),
    LPAREN(TC.OTHER, "("),
    RPAREN(TC.OTHER, ")"),
    LBRACE(TC.OTHER, "{"),
    RBRACE(TC.OTHER, "}"),
    ERROR(TC.OTHER, "[error]"),
    EOF(TC.OTHER, "EOF");

    /** Maps the string representation of a token to its enum. */
    private static final Map<String, TokenType> stringToTypeMap;

    // static initializer to initialize the values of stringToTypeMap
    static {
        final Map<String, TokenType> temp = new HashMap<>();
        for (TokenType t : TokenType.values()) {
            temp.put(t.stringRep, t);
        }
        stringToTypeMap = Collections.unmodifiableMap(temp);
    }

    private final TC category;

    private final String stringRep;

    private TokenType(TC tcat, String s) {
        assert tcat != null : "TokenType must have a category";
        assert s != null : "TokenType must have a string representation";
        category = tcat;
        stringRep = s;
    }

    public TC category() {return category;}

    public static TokenType getTypeFromString(String rep) {return stringToTypeMap.get(rep);}

    @Override
    public String toString() {return stringRep;}
}
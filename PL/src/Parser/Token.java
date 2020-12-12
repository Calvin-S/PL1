package Parser;

import java.util.InputMismatchException;

/** A Token represents a legal token (symbol) in the critter language */
class Token {

    /** The type of this Token. */
    private final TokenType type;

    /**
     * The line number in the input file containing this token. Must be positive, or -1 for the
     * error token.
     */
    private final int lineNo;

    /**
     * Create a token with the specified type.
     *
     * @param type The ID of the desired token type. Checks {@code type != null}.
     * @param lineNum The line number in the input file containing this token, or {code -1} for an
     *     error token. Checks {@code lineNum > 0} or {@code lineNum == -1}.
     */
    Token(TokenType t, int lineNum) {
        assert t != null : "TokenType may not be null";
        assert lineNum > 0 || lineNum == -1
                : "lineNum must be positive or -1 for an error token, but is " + lineNum;
        type = t;
        lineNo = lineNum;
    }

    /** @return The type of this token */
    TokenType getType() {
        return type;
    }

    /** @return The line number in the input file of this token. */
    int lineNumber() {
        return lineNo;
    }

    boolean isType() {
    	return isNum() || isBool() || isString() || isNull();
    }
    
    boolean isNull() {
        return type == TokenType.NULL;
    }
    
    boolean isNum() {
        return type == TokenType.NUM;
    }
    
    boolean isBool() {
    	return type == TokenType.TRUE || type == TokenType.FALSE;
    }
    
    boolean isString() {
    	return type == TokenType.STRING;
    }
    boolean isVar() {
    	return type == TokenType.VAR;
    }
    boolean isFun() {
    	return type == TokenType.FUN;
    }
    boolean isCall() {
    	return type == TokenType.CALL;
    }
    /**
     * Returns the number token associated with this Token.
     *
     * @return The number token associated with this token.
     * @throws InputMismatchException if this token is not of number type
     */
    NumToken toNumToken() {
        if (isNum()) {
            return (NumToken) this;
        }
        throw new InputMismatchException("Token is not a number.");
    }
    
    StringToken toStringToken() {
    	if (isString()) {
    		return (StringToken) this;
    	}
    	throw new InputMismatchException("Token is not a string.");
    }
    
    VarToken toVarToken() {
    	if (isVar()) {
    		return (VarToken) this;
    	}
    	throw new InputMismatchException("Token is not a var.");
    }
    
    FunToken toFunToken() {
    	if (isFun()) {
    		return (FunToken) this;
    	}
    	throw new InputMismatchException("Token is not a fun.");
    }
    
    CallToken toCallToken() {
    	if (isCall()) {
    		return (CallToken) this;
    	}
    	throw new InputMismatchException("Token is not a call.");
    }

    @Override
    public String toString() {
        return type.toString();
    }

    /** A NumToken is a token containing a number. */
    static class NumToken extends Token {

        /** The int value this token represents. */
        private final long value;

        /**
         * Constructs a new {@code NumToken} representing the integer value {@code v} on line {@code
         * lineNum}.
         *
         * @param v the integer value this token represents
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that a {@code NumToken} is not an error token, so the line number
         *     must be positive.
         */
        NumToken(long v, int lineNum) {
            super(TokenType.NUM, lineNum);
            assert lineNum > 0 : "NumToken line number must be positive.";
            value = v;
        }

        /**
         * Returns the value of the number this token represents.
         *
         * @return the value of the number this token represents
         */
        long getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
    
    static class StringToken extends Token {

        /** The string value this token represents. */
        private final String value;

        /**
         * Constructs a new {@code NumToken} representing the String value {@code v} on line {@code
         * lineNum}.
         *
         * @param v the String value this token represents
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that a {@code StringToken} is not an error token, so the line number
         *     must be positive.
         */
        StringToken(String v, int lineNum) {
            super(TokenType.STRING, lineNum);
            assert lineNum > 0 : "StringToken line number must be positive.";
            value = v;
        }

        /**
         * Returns the value of the number this token represents.
         *
         * @return the value of the number this token represents
         */
        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    static class VarToken extends Token {

        /** The string value this token represents. */
        private final String value;

        /**
         * Constructs a new {@code NumToken} representing the String value {@code v} on line {@code
         * lineNum}.
         *
         * @param v the String value this token represents
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that a {@code StringToken} is not an error token, so the line number
         *     must be positive.
         */
        VarToken(String v, int lineNum) {
            super(TokenType.VAR, lineNum);
            assert lineNum > 0 : "VarToken line number must be positive.";
            value = v;
        }

        /**
         * Returns the value of the number this token represents.
         *
         * @return the value of the number this token represents
         */
        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    static class FunToken extends Token {

        /** The string value this token represents. */
        private final String value;

        /**
         * Constructs a new {@code NumToken} representing the String value {@code v} on line {@code
         * lineNum}.
         *
         * @param v the String value this token represents
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that a {@code StringToken} is not an error token, so the line number
         *     must be positive.
         */
        FunToken(String v, int lineNum) {
            super(TokenType.FUN, lineNum);
            assert lineNum > 0 : "VarToken line number must be positive.";
            value = v;
        }

        /**
         * Returns the value of the number this token represents.
         *
         * @return the value of the number this token represents
         */
        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    static class CallToken extends Token {

        /** The string value this token represents. */
        private final String value;

        /**
         * Constructs a new {@code NumToken} representing the String value {@code v} on line {@code
         * lineNum}.
         *
         * @param v the String value this token represents
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that a {@code StringToken} is not an error token, so the line number
         *     must be positive.
         */
        CallToken(String v, int lineNum) {
            super(TokenType.CALL, lineNum);
            assert lineNum > 0 : "VarToken line number must be positive.";
            value = v;
        }

        /**
         * Returns the value of the number this token represents.
         *
         * @return the value of the number this token represents
         */
        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /** Helper class to represent an end of file token. */
    static class EOFToken extends Token {

        /** Contents of the string builder at the time the EOF was encountered. */
        private final String builderContents;

        /**
         * Constructs a new {@code EOFToken} at line {@code lineNum} containing the contents of the
         * string builder when the EOF was encountered.
         *
         * @param sbContents contents of the string builder at the time the EOF was encountered
         * @param lineNum The line number in the input file containing this token. Checks {@code
         *     lineNum > 0}. Note that an {@code EOFToken} is not an error token, so the line number
         *     must be positive.
         */
        EOFToken(String sbContents, int lineNum) {
            super(TokenType.EOF, lineNum);
            assert lineNum > 0 : "EOFToken lineNum must be positive.";
            builderContents = sbContents;
        }

        /**
         * Returns the contents of the string builder when the EOF was encountered.
         *
         * @return the contents of the string builder when the EOF was encountered
         */
        String getBufferContents() {
            return builderContents;
        }

        @Override
        public String toString() {
            return "Buffer contained <" + builderContents + "> at EOF";
        }
    }

    /** Helper class to represent an error token. */
    static class ErrorToken extends Token {

        /** String value stored by this error token. */
        private final String value;

        /**
         * Constructs a new error token containing the value {@code v}.
         *
         * @param v String value for this error token.
         */
        ErrorToken(String v, int lineNum) {
            super(TokenType.ERROR, lineNum);
            value = v;
        }

        /**
         * Returns the string value stored by this error token.
         *
         * @return the string value stored by this error token
         */
        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("Syntax error at line %d]: %s", lineNumber(), value);
        }
    }
}
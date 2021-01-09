package Parser;

import java.io.IOException;
import java.io.Reader;

public class LookAheadBuffer {

    private final Reader in;
    private final char[] buf;
    private int hd = 0; // index we pop from
    private int tl = 0; // index we push to
    private final int capacity;
    public static final char EOF = 0x7F; // ascii DEL - indicates end of stream

    LookAheadBuffer(int capacity, Reader in) {
        this.capacity = capacity;
        this.in = in;
        buf = new char[capacity + 1]; 
    }
    
    public char next() throws IOException{
    	if (size() != 0) return pop();
    	int c = in.read();
    	return c == -1 ? EOF : (char)c;
    }
    
    public char peek(int n) throws IOException{
    	while (size() <= n) {
            int r = in.read();
            char c = r == -1 ? EOF : (char) r;
            push(c);
        }
        return buf[(hd + n) % buf.length];
    }
    
    public char peek() throws IOException {
    	return peek(0);
    }
    
    private int size() throws IOException {
    	return (tl - hd + buf.length) % buf.length;
    }
    
    public char scanAndPeek() throws IOException {
    	next();
    	return peek();
    }
    
    private void push(char c) {
    	buf[tl] = c;
    	tl = (tl + 1) % buf.length;
    }
    private char pop() {
    	char popped = buf[hd];
    	hd = (hd + 1) % buf.length;
    	return popped;
    }
}

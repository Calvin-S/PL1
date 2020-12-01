import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import Parser.Parser;
import ast.Program;

public class Main {
	public static void main(String args[]) {
		
		InputStream in = ClassLoader.getSystemResourceAsStream("Examples/test2.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));

		Parser parser = new Parser();
		Program prog = parser.parse(r);
		System.out.println(prog);
	}
}

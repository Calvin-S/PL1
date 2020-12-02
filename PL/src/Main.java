import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import Parser.Parser;
import Parser.SyntaxError;
import ast.Program;
import interpreter.Interpreter;
import interpreter.Value;

public class Main {
	public static void main(String args[]) {
		
		InputStream in = ClassLoader.getSystemResourceAsStream("Examples/test0.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));

		Parser parser = new Parser();
		Program prog = parser.parse(r);
		System.out.println(prog);

//		Interpreter i = new Interpreter();
//		Value v;
//		try {
//			v = i.evaluateProg(prog);
//			System.out.println(v.toString());
//		} catch (SyntaxError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

	}
}

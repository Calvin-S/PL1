import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import Parser.Parser;
import Parser.SyntaxError;
import ast.Program;
import interpreter.Interpreter;
import interpreter.Value;

public class Main {
	public static void main(String args[]) {
		
		InputStream in = ClassLoader.getSystemResourceAsStream("Examples/test1.txt");
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
//
//		System.out.println("STORE");
//
//		HashMap<String, Value> store = i.getStore();
//		
//		if (store != null) {
//			for (String key : store.keySet()) {
//				System.out.println(key + " : " + store.get(key));
//			}
//		} else {
//			System.out.println("store was never initialized");
//		}

	}
}

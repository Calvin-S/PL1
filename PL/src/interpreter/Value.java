package interpreter;

public class Value {

	private int[] type = { 0, 0, 0 };

	private int intVal;
	private String stringVal;
	private boolean boolVal;

	public Value(int i) {
		intVal = i;
		stringVal = null;
		boolVal = false;

		type[0] = 1;
	}

	public Value(String s) {
		intVal = 0;
		stringVal = null;
		boolVal = false;

		type[1] = 1;
	}

	public Value(boolean b) {
		intVal = 0;
		stringVal = null;
		boolVal = b;

		type[2] = 1;
	}

	// use the type from this function to call the correct function to get the value
	// of v
	public String getType() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0) {
			return "int";
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0) {
			return "string";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1) {
			return "bool";
		} else {
			return "error: v has multiple types";
		}
	}

	public int getInt() {
		return intVal;
	}

	public String getString() {
		return stringVal;
	}

	public boolean getBool() {
		return boolVal;
	}

	public String toString() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0) {
			return Integer.toString(intVal);
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0) {
			return stringVal;
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1) {
			return Boolean.toString(boolVal);
		} else {
			return "something went wrong with value";
		}
	}


}

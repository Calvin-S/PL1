package interpreter;

public class Value {

	private int[] type = { 0, 0, 0, 0 };

	private long intVal;
	private String stringVal;
	private boolean boolVal;
	private boolean nullVal;

	public Value(long i) {
		intVal = i;
		stringVal = "";
		boolVal = false;

		type[0] = 1;
	}

	public Value(String s) {
		intVal = 0;
		stringVal = "";
		boolVal = false;

		type[1] = 1;
	}

	public Value(boolean b) {
		intVal = 0;
		stringVal = "";
		boolVal = b;

		type[2] = 1;
	}

	public Value() {
		intVal = 0;
		stringVal = "";
		boolVal = false;

		nullVal = true;

		type[3] = 1;
	}

	// use the type from this function to call the correct function to get the value
	// of v
	public String getType() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0 && type[3] == 0) {
			return "int";
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0 && type[3] == 0) {
			return "string";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1 && type[3] == 0) {
			return "bool";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 1) {
			return "null";
		} else {
			return "error: v has multiple types";
		}
	}

	// next four methods should only be used AFTER using getType to confirm the type
	// of the value
	public long getInt() {
		return intVal;
	}

	public String getString() {
		return stringVal;
	}

	public boolean getBool() {
		return boolVal;
	}

	public boolean isNull() {
		return nullVal;
	}


	public String toString() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0 && type[3] == 0) {
			return String.valueOf(intVal);
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0 && type[3] == 0) {
			return stringVal;
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1 && type[3] == 0) {
			return Boolean.toString(boolVal);
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 1) {
			return "NULL";
		} else {
			return "something went wrong with value";
		}
	}


}

package interpreter;

import java.util.ArrayList;

public class Value {

	// int, string, bool, null, list
	private int[] type = { 0, 0, 0, 0, 0 };

	private long intVal;
	private String stringVal;
	private boolean boolVal;
	private boolean nullVal;
	private ArrayList<Value> listVal;

	public Value(long i) {
		intVal = i;
		stringVal = "";
		boolVal = false;
		nullVal = false;
		listVal = null;

		type[0] = 1;
	}

	public Value(String s) {
		intVal = 0;
		stringVal = s;
		boolVal = false;
		nullVal = false;
		listVal = null;

		type[1] = 1;
	}

	public Value(boolean b) {
		intVal = 0;
		stringVal = "";
		boolVal = b;
		nullVal = false;
		listVal = null;

		type[2] = 1;
	}

	public Value() {
		intVal = 0;
		stringVal = "";
		boolVal = false;

		nullVal = true;
		listVal = null;

		type[3] = 1;
	}

	public Value(ArrayList<Value> v) {
		intVal = 0;
		stringVal = "";
		boolVal = false;
		nullVal = false;

		listVal = v;

		type[4] = 1;
	}

	// use the type from this function to call the correct function to get the value
	// of v
	public String getType() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0 && type[3] == 0 && type[4] == 0) {
			return "int";
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0 && type[3] == 0 && type[4] == 0) {
			return "string";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1 && type[3] == 0 && type[4] == 0) {
			return "bool";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 1 && type[4] == 0) {
			return "null";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 0 && type[4] == 1) {
			return "list";
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

	public ArrayList<Value> getList() {
		return listVal;
	}

	public String toString() {
		if (type[0] == 1 && type[1] == 0 && type[2] == 0 && type[3] == 0 && type[4] == 0) {
			return String.valueOf(intVal);
		} else if (type[0] == 0 && type[1] == 1 && type[2] == 0 && type[3] == 0 && type[4] == 0) {
			return stringVal;
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 1 && type[3] == 0 && type[4] == 0) {
			return Boolean.toString(boolVal);
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 1 && type[4] == 0) {
			return "NULL";
		} else if (type[0] == 0 && type[1] == 0 && type[2] == 0 && type[3] == 0 && type[4] == 1) {
			StringBuilder sb = new StringBuilder("[");
			for (Value v : listVal) {
				sb.append(v.toString() + ", ");
			}
			String listTemp = sb.toString();
			listTemp = listTemp.substring(0, listTemp.length() - 2);

			listTemp = listTemp + "]";
			return listTemp;

		} else {
			return "something went wrong with value";
		}
	}


}

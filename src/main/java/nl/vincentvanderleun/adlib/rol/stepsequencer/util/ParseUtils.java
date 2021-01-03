package nl.vincentvanderleun.adlib.rol.stepsequencer.util;

public class ParseUtils {

	public static int parseInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Expected integer value", ex);
		}
	}
	
	public static float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Expected float value", ex);
		}
	}
	
	public static boolean parseBoolean(String value) {
		if(value.equalsIgnoreCase("true") || value.equals("1")) {
			return true;
		} else if (value.equalsIgnoreCase("false") || value.equals("0")) {
			return false;
		} else {
			throw new IllegalArgumentException("Expected boolean value (\"true\", '\"false\", \"1\", \"0\"...)");
		}
	}
}

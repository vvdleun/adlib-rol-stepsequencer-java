package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class ValueParser {

	public int parseInteger(String value, long lineNumber) throws ParseException {
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException ex) {
			throw new ParseException("Expected integer value at line " + lineNumber, ex);
		}
	}
	
	public float parseFloat(String value, long lineNumber) throws ParseException {
		try {
			return Float.parseFloat(value);
		} catch(NumberFormatException ex) {
			throw new ParseException("Expected float value at line " + lineNumber, ex);
		}
	}

}

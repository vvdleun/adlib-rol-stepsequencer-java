package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.util.ParseUtils;

public class ValueParser {

	public int parseInteger(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseInteger(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected integer value at line " + lineNumber, ex);
		}
	}
	
	public float parseFloat(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseFloat(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected float value at line " + lineNumber, ex);
		}
	}
	
	public boolean parseBoolean(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseBoolean(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected boolean value (\"true\", '\"false\", \"1\", \"0\"...) at line " + lineNumber);
		}
	}

}

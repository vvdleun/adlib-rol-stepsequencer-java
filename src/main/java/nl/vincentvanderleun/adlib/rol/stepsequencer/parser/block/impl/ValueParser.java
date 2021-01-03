package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.util.ParseUtils;

public class ValueParser {

	public static int parseNoteDuration(String duration, int ticksPerBeat, int beatsPerMeasure, long lineNumber) throws ParseException {
		// Numeric value: ticks
		// Number with "b" suffix ("4b") --> 4 beats
		// Number with "m" suffix ("4m") --> 4 measures
		final String inputValue = duration.toLowerCase();
		
		String toConvert = inputValue;
		if(toConvert.endsWith("b") || toConvert.endsWith("m")) {
			toConvert = toConvert.substring(0, toConvert.length() - 1);
		}

		int parsedValue;
		try {
			parsedValue = parseInteger(toConvert, lineNumber);
		} catch(NumberFormatException ex) {
			throw new ParseException("Expected note duration, a numeric value with optionally \"b\" or \"m\" suffix, but parsed \""
					+ duration + "\" instead at line " + lineNumber, ex);
		}

		if(inputValue.endsWith("b")) {
			parsedValue = parsedValue * ticksPerBeat;
		} else if(inputValue.endsWith("m")) {
			parsedValue = parsedValue * ticksPerBeat * beatsPerMeasure;
		}

		if(parsedValue <= 0) {
			throw new ParseException("Duration of must be higher than 0 ticks at line " + lineNumber);
		}

		return parsedValue;
	}
	
	public static int parseInteger(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseInteger(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected integer value at line " + lineNumber, ex);
		}
	}
	
	public static float parseFloat(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseFloat(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected float value at line " + lineNumber, ex);
		}
	}
	
	public static boolean parseBoolean(String value, long lineNumber) throws ParseException {
		try {
			return ParseUtils.parseBoolean(value);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Expected boolean value (\"true\", '\"false\", \"1\", \"0\"...) at line " + lineNumber);
		}
	}

}

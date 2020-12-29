package nl.vincentvanderleun.adlib.rol.stepsequencer.util;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;

public class ParseUtils {

	public static int parseDuration(String functionName, Object argument, int ticksPerBeat, int beatsPerMeasure) throws CompileException {
		// Numeric value: ticks
		// Number with "b" suffix ("4b") --> 4 beats
		// Number with "m" suffix ("4m") --> 4 measures
		final String inputValue = ((String)argument).toLowerCase();
		
		String toConvert = inputValue;
		if(toConvert.endsWith("b") || toConvert.endsWith("m")) {
			toConvert = toConvert.substring(0, toConvert.length() - 1);
		}

		int parsedValue;
		try {
			parsedValue = Integer.parseInt(toConvert);
		} catch(NumberFormatException ex) {
			throw new CompileException("Expected integer value as parameter of function \""
					+ functionName + "\", with optionally \"b\" or \"m\" suffix, but parsed \""
					+ argument + "\" instead.", ex);
		}

		if(inputValue.endsWith("b")) {
			parsedValue = parsedValue * ticksPerBeat;
		} else if(inputValue.endsWith("m")) {
			parsedValue = parsedValue * ticksPerBeat * beatsPerMeasure;
		}
		
		return parsedValue;
	}

}

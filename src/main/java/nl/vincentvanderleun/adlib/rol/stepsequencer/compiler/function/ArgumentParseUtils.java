package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;

public class ArgumentParseUtils {

	public static void checkArgumentCount(String functionName, List<String> arguments, int expectedNumberOfArguments) throws CompileException {
		if(arguments.size() != expectedNumberOfArguments) {
			throw new CompileException("Function " + functionName + " expects " + expectedNumberOfArguments
					+ "arguments , but got " + arguments + " instead.");
		}
	}
	
	public static int parseTicksArgument(String functionName, Object argument, CompiledSong song) throws CompileException {
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
			parsedValue = parsedValue * song.getTicksPerBeat();
		} else if(inputValue.endsWith("m")) {
			parsedValue = parsedValue * song.getTicksPerBeat() * song.getBeatsPerMeasure();
		}
		
		return parsedValue;
	}
}

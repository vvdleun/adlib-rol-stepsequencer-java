package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class FunctionArgumentHelper {

	public static void checkArgumentCount(String functionName, List<String> arguments, int expectedNumberOfArguments, long lineNumber) throws ParseException {
		if(arguments.size() != expectedNumberOfArguments) {
			throw new ParseException("FunctionCall " + functionName + " expects " + expectedNumberOfArguments
					+ "arguments , but got " + arguments.size() + " instead at line " + lineNumber);
		}
	}

}

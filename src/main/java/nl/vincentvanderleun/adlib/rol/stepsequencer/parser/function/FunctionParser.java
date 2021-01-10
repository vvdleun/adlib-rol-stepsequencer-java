package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import java.util.Scanner;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class FunctionParser {
	private final Scanner scanner;
	
	public FunctionParser(Scanner scanner) {
		this.scanner = scanner;
	}

	public ParsableFunction parse(String firstToken, long lineNumber) throws ParseException {
		// First token must at least contain function name and opening "("
		final int openIndex = firstToken.indexOf('(');
		if(openIndex <= 0) {
			return null;
		}

		final String functionName = firstToken.substring(0, openIndex);

		String nextToken = firstToken.substring(openIndex + 1);

		final var arguments = new StringBuilder(nextToken);

		while (!foundFunctionEnd(arguments) && scanner.hasNext()) {
			nextToken = scanner.next();
			arguments.append(nextToken.trim());
		}
		
		if(!foundFunctionEnd(arguments)) {
			throw new ParseException(
					"Could not find closing \")\" parameter on function call \""
			+ functionName
			+ "\" on line "
			+ lineNumber);
		}
		
		arguments.delete(arguments.length() - 1, arguments.length());
		
		return new ParsableFunction(functionName, arguments.toString());
	}
	
	private boolean foundFunctionEnd(StringBuilder arguments) {
		if(arguments.length() == 0) {
			return false;
		}
		return arguments.charAt(arguments.length() - 1) == ')';
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import java.util.LinkedList;
import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public abstract class ParsedFunctionParser<T> {
	protected final ParsableFunction parsableFunction;
	protected final ArgumentParser argumentParser;
	protected final long lineNumber;
	protected final List<Object> parsedArguments;
	
	public ParsedFunctionParser(ParsableFunction parsableFunction, long lineNumber) {
		this.parsableFunction = parsableFunction;
		this.argumentParser = new ArgumentParser(
				parsableFunction.getName(),
				parsableFunction.getRawArguments(),
				lineNumber);
		this.lineNumber = lineNumber;
		
		this.parsedArguments = new LinkedList<>();
	}
	
	public abstract T parse() throws ParseException;
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import java.util.regex.Pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.util.ParseUtils;

public class ArgumentParser {
	private final String functionName;
	private final long lineNumber;
	private final String[] rawArguments;
	
	private int index;
	
	public ArgumentParser(String functionName, String rawArguments, long lineNumber) {
		this.functionName = functionName;
		this.lineNumber = lineNumber;

		if(rawArguments == null || rawArguments.trim().equals("")) {
			this.rawArguments = null;
		} else {
			this.rawArguments = rawArguments.trim().split(Pattern.quote(","));
		}

		this.index = 0;
	}

	public boolean hasMoreArguments() {
		return rawArguments != null && index < rawArguments.length;
	}
	
	public int parseNextArgumentAsInteger() throws ParseException {
		final String nextArgument = parseNextArgument();
		try {
			return ParseUtils.parseInteger(nextArgument);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Integer expected in argument #"
					+ (index + 1)
					+ " of function call \""
					+ functionName
					+ "\" at line "
					+ lineNumber);
		}
	}

	public float parseNextArgumentAsFloat() throws ParseException {
		final String nextArgument = parseNextArgument();
		try {
			return ParseUtils.parseFloat(nextArgument);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Float expected in argument #"
					+ (index + 1)
					+ " of function call \""
					+ functionName
					+ "\" at line "
					+ lineNumber);
		}
	}

	public boolean parseNextArgumentAsBoolean() throws ParseException {
		final String nextArgument = parseNextArgument();
		try {
			return ParseUtils.parseBoolean(nextArgument);
		} catch(IllegalArgumentException ex) {
			throw new ParseException("Boolean expected in argument #"
					+ (index + 1)
					+ " of function call \""
					+ functionName
					+ "\" at line "
					+ lineNumber);
		}
	}

	public String parseNextArgument() throws ParseException {
		if(!hasMoreArguments()) {
			throw new ParseException("Argument expected in function call \"" + functionName + "\" at line " + lineNumber);
		}
		return rawArguments[index++].trim();
	}
}

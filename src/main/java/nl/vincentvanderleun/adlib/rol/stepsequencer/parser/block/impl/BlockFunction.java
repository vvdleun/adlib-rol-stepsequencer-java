package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import java.util.Arrays;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class BlockFunction {
	private final String name;
	private final String[] arguments;
	private final ValueParser valueParser;
	private final long lineNumber;
	
	public BlockFunction(String name, String[] arguments, long lineNumber) {
		this.name = name;
		this.arguments = arguments;
		this.lineNumber = lineNumber;
		this.valueParser = new ValueParser();
	}

	public String getName() {
		return name;
	}

	public int getCountArguments() {
		return arguments.length;
	}
	
	public String getArgument(int index) {
		return arguments[index];
	}

	public int parseArgumentAsInteger(int index) throws ParseException {
		// TODO also report column number, or at least the token
		return valueParser.parseInteger(arguments[index], lineNumber);
	}
	
	public float parseArgumentAsFloat(int index) throws ParseException {
		// TODO also report column number, or at least the token
		return valueParser.parseFloat(arguments[index], lineNumber);
	}

	@Override
	public String toString() {
		return "BlockFunction [name=" + name + ", arguments=" + Arrays.toString(arguments) + ", valueParser="
				+ valueParser + ", lineNumber=" + lineNumber + "]";
	}
}

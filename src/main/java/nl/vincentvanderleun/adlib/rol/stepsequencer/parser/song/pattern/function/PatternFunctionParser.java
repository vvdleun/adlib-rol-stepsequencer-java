package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsedFunctionParser;

public abstract class PatternFunctionParser extends ParsedFunctionParser<PatternFunction> {
	protected final PatternFunctionType functionType;
	
	public PatternFunctionParser(PatternFunctionType functionType, ParsableFunction parsableFunction, long lineNumber) {
		super(parsableFunction, lineNumber);

		this.functionType = functionType;
	}
}

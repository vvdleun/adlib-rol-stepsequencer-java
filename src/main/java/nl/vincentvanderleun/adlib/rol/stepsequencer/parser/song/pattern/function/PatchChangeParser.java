package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;

public class PatchChangeParser extends PatternFunctionParser {

	public PatchChangeParser(ParsableFunction parsableFunction, long lineNumber) {
		super(PatternFunctionType.PATCH_CHANGE, parsableFunction, lineNumber);
	}
	
	@Override
	public PatternFunction parse() throws ParseException {
		final String patch = argumentParser.parseNextArgument();

		if (argumentParser.hasMoreArguments()) {
			throw new ParseException("Did not expect more than one parameter in function call \""
					+ parsableFunction.getName()
					+ "\" at line " + lineNumber);
		}
		
		parsedArguments.add(patch);
		
		return new PatternFunction(PatternFunctionType.PATCH_CHANGE, parsedArguments);
	}
}

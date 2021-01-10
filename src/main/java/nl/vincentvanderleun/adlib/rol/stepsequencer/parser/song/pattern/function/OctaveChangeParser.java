package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;

public class OctaveChangeParser extends PatternFunctionParser {

	public OctaveChangeParser(ParsableFunction parsableFunction, long lineNumber) {
		super(PatternFunctionType.OCTAVE_CHANGE, parsableFunction, lineNumber);
	}
	
	@Override
	public PatternFunction parse() throws ParseException {
		final int octave = argumentParser.parseNextArgumentAsInteger();

		if (argumentParser.hasMoreArguments()) {
			throw new ParseException("Did not expect more than one parameter in function call \""
					+ parsableFunction.getName()
					+ "\" at line " + lineNumber);
		}
		
		parsedArguments.add(octave);
		
		return new PatternFunction(PatternFunctionType.OCTAVE_CHANGE, parsedArguments);
	}
}

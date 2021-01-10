package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsedFunctionParser;

public abstract class TrackFunctionParser extends ParsedFunctionParser<TrackFunction> {
	
	public TrackFunctionParser(ParsableFunction parsableFunction, long lineNumber) {
		super(parsableFunction, lineNumber);
	}
	
}

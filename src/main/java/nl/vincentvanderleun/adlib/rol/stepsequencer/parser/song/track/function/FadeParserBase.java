package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.ValueParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;

public abstract class FadeParserBase extends TrackFunctionParser {
	private final SongHeader songHeader;
	private final TrackFunctionType functionType;
	
	public FadeParserBase(TrackFunctionType functionType, ParsableFunction parsableFunction, SongHeader songHeader, long lineNumber) {
		super(parsableFunction, lineNumber);

		this.functionType = functionType;
		this.songHeader = songHeader;
	}

	@Override
	public TrackFunction parse() throws ParseException {
		final String rawDuration = argumentParser.parseNextArgument();

		if (argumentParser.hasMoreArguments()) {
			throw new ParseException("Did not expect more than one parameter in function call \""
					+ parsableFunction.getName()
					+ "\" at line " + lineNumber);
		}
		
		int durationTicks = ValueParser.parseNoteDuration(
				rawDuration,
				songHeader.getTicksPerBeat(),
				songHeader.getBeatsPerMeasure(),
				lineNumber);
		
		parsedArguments.add(durationTicks);
		
		return new TrackFunction(functionType, parsedArguments);
	}
}

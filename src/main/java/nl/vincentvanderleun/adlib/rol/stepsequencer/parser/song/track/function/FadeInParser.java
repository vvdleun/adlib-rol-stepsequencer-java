package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;

public class FadeInParser extends FadeParserBase {
	
	public FadeInParser(ParsableFunction parsableFunction, SongHeader songHeader, long lineNumber) {
		super(TrackFunctionType.FADE_IN, parsableFunction, songHeader, lineNumber);
	}

}

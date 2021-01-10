package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;

public class FadeOutParser extends FadeParserBase {
	
	public FadeOutParser(ParsableFunction parsableFunction, SongHeader songHeader, long lineNumber) {
		super(TrackFunctionType.FADE_OUT, parsableFunction, songHeader, lineNumber);
	}

}

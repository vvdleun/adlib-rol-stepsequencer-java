package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsedFunction;

public class TrackFunction extends ParsedFunction<TrackFunctionType> {
	
	public TrackFunction(TrackFunctionType functionType, List<Object> arguments) {
		super(functionType, arguments);
	}

}

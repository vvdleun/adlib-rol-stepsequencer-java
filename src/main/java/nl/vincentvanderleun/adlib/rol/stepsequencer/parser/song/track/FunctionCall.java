package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function.TrackFunction;

public class FunctionCall extends Event {
	private TrackFunction function;

	public FunctionCall(TrackFunction function) {
		super(EventType.FUNCTION_CALL);
		this.function = function;
	}
	
	public TrackFunction getFunction() {
		return function;
	}
}

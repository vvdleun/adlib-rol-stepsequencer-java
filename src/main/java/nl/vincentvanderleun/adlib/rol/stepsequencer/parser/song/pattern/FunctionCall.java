package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function.PatternFunction;

public class FunctionCall extends Event {
	private PatternFunction function;

	public FunctionCall(PatternFunction function) {
		super(EventType.FUNCTION_CALL);
		this.function = function;
	}
	
	public PatternFunction getFunction() {
		return function;
	}
}

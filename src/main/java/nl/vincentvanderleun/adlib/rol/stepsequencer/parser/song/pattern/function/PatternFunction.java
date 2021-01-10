package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsedFunction;

public class PatternFunction extends ParsedFunction<PatternFunctionType> {
	
	public PatternFunction(PatternFunctionType functionType, List<Object> arguments) {
		super(functionType, arguments);
	}

}

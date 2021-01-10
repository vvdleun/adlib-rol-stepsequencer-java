package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import java.util.List;

public abstract class ParsedFunction<T> {
	private final T functionType;
	private final List<Object> arguments;

	public ParsedFunction(T functionType, List<Object> arguments) {
		this.functionType = functionType;
		this.arguments = arguments;
	}

	public T getFunctionType() {
		return functionType;
	}

	public List<Object> getArguments() {
		return arguments;
	}
}

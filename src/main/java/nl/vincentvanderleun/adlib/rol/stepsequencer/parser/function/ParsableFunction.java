package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

/**
 * Represents a function call, which individual arguments have not been parsed yet.
 *
 * @author Vincent
 */
public class ParsableFunction {
	private final String name;
	private final String rawArguments;

	public ParsableFunction(String name, String rawArguments) {
		this.name = name;
		this.rawArguments = rawArguments;
	}

	public String getName() {
		return name;
	}

	public String getRawArguments() {
		return rawArguments;
	}
}

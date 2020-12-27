package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track;

import java.util.List;
import java.util.Objects;

public class FunctionCall extends Event {
	private final String functionName;
	private final List<String> arguments;

	public FunctionCall(String functionName, List<String> arguments) {
		super(EventType.FUNCTION_CALL);
		this.functionName = functionName;
		this.arguments = arguments;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public List<String> getArguments() {
		return arguments;
	}

	@Override
	public int hashCode() {
		return Objects.hash(arguments, functionName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionCall other = (FunctionCall) obj;
		return Objects.equals(arguments, other.arguments) && Objects.equals(functionName, other.functionName);
	}

	@Override
	public String toString() {
		return "FunctionCall [functionName=" + functionName + ", arguments=" + arguments + "]";
	}
	
	
}

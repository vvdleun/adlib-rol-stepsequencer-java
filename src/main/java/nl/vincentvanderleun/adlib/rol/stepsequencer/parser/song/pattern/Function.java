package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

import java.util.ArrayList;
import java.util.List;

public class Function extends Event {
	private final String functionName;
	private final List<Object> arguments;
	
	public Function(String functionName, Object... arguments) {
		super(EventType.FUNCTION);

		this.functionName = functionName;
		
		var argumentsList = new ArrayList<Object>();
		for(var argument : arguments) {
			argumentsList.add(argument);
		}
		
		this.arguments = argumentsList;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public List<Object> getArguments() {
		return arguments;
	}
}

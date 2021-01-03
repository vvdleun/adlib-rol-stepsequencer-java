package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FunctionCall extends Event {
	private final String functionName;
	private final List<Object> arguments;
	
	public FunctionCall(String functionName, Object... arguments) {
		super(EventType.FUNCTION);

		this.functionName = functionName;
		
		var argumentsList = new ArrayList<Object>();
		for(var argument : arguments) {
			if(argument instanceof Collection) {
				// Easy mistake to make, due to the varargs arguments and and can take terrible long to debug...
				throw new IllegalStateException("Having a collection as function argument is not supported for now");
			}
			
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

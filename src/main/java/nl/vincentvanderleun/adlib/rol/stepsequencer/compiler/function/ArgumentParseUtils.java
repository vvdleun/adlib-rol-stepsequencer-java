package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;

public class ArgumentParseUtils {

	public static void checkArgumentCount(String functionName, List<String> arguments, int expectedNumberOfArguments) throws CompileException {
		if(arguments.size() != expectedNumberOfArguments) {
			throw new CompileException("Function " + functionName + " expects " + expectedNumberOfArguments
					+ "arguments , but got " + arguments + " instead.");
		}
	}

	
	
}

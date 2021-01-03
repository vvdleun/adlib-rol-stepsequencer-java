package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.pattern;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;

public class Octave extends CompilablePatternFunction {
	private static final String FUNCTION_NAME = "Octave";
	
	public Octave() {
		super(FUNCTION_NAME);
	}
	
	@Override
	public void execute(Track track, CompilerContext context, List<Object> arguments) throws CompileException {
		context.octave = (Integer)arguments.get(0);
	}
}

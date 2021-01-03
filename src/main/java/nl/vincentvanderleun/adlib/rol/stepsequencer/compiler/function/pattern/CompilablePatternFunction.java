package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.pattern;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.CompilableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;

public abstract class CompilablePatternFunction extends CompilableFunction {

	public CompilablePatternFunction(String name) {
		super(name);
	}

	public abstract void execute(Track track, CompilerContext context, List<Object> arguments) throws CompileException;
}

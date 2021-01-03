package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.CompilableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;

public abstract class CompilableTrackFunction extends CompilableFunction {

	public CompilableTrackFunction(String name) {
		super(name);
	}

	public abstract void execute(Track track, int tick, List<Object> arguments) throws CompileException;
}

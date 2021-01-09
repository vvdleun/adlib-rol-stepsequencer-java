package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;

public abstract class FadeBase extends CompilableTrackFunction {
	public FadeBase(String name) {
		super(name);
	}

	@Override
	public void execute(Track track, int tick, List<Object> arguments) throws CompileException {
		final int duration = (Integer)arguments.get(0);

		final int endTick = tick + duration - 1;

		createFadeEffect(track, tick, endTick, duration);
	}

	protected abstract void createFadeEffect(Track track, int tick, int endTick, int numberEvents);
}

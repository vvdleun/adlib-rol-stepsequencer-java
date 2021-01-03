package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import static nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.ArgumentParseUtils.checkArgumentCount;
import static nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerParseUtils.parseDuration;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;

public abstract class FadeBase extends CompilableTrackFunction {
	public FadeBase(String name) {
		super(name);
	}

	@Override
	public void execute(Track track, int tick, List<String> arguments) throws CompileException {
		checkArgumentCount(name, arguments, 1);

		final int duration = parseDuration(
				name,
				arguments.get(0),
				track.getSong().getTicksPerBeat(),
				track.getSong().getBeatsPerMeasure());

		if(duration <= 0) {
			throw new CompileException("Duration of " + name + " must be higher than 0 ticks");
		}

		final int endTick = tick + duration - 1;

		createFadeEffect(track, tick, endTick);
	}

	protected abstract void createFadeEffect(Track track, int tick, int endTick);
}

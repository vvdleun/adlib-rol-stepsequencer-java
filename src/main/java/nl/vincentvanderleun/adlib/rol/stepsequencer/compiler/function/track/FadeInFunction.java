package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;

import static nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.ArgumentParseUtils.checkArgumentCount;
import static nl.vincentvanderleun.adlib.rol.stepsequencer.util.ParseUtils.parseDuration;;

public class FadeInFunction extends CompilableTrackFunction {
	private static final String FUNCTION_NAME = "fade-in";
	
	public FadeInFunction() {
		super(FUNCTION_NAME);
	}

	@Override
	public void execute(Track track, int tick, List<String> arguments) throws CompileException {
		checkArgumentCount(FUNCTION_NAME, arguments, 1);
		final int duration = parseDuration(
				FUNCTION_NAME,
				arguments.get(0),
				track.getSong().getTicksPerBeat(),
				track.getSong().getBeatsPerMeasure());

		if(duration <= 0) {
			throw new CompileException("Duration of " + FUNCTION_NAME + " must be higher than 0 ticks");
		}

		track.getChannels().forEach(channel -> createFadeIn(channel, tick, duration));
	}
	
	private void createFadeIn(Channel channel, int startTick, int duration) {
		final int endTick = startTick + duration - 1;
		final float toVolume = channel.getEventsAtOrBeforeTick(endTick).getVolume();
		final float step = toVolume / duration;
		
		float nextVolume = 0.0f;
		for(int tick = startTick; tick < endTick; tick++) {
			channel.addEvent(tick, new VolumeMultiplierEvent(nextVolume));
			nextVolume += step;
			if(nextVolume > 1.0f) {
				nextVolume = 1.0f;
			}
		}
		channel.addEvent(endTick, new VolumeMultiplierEvent(toVolume));
	}
}

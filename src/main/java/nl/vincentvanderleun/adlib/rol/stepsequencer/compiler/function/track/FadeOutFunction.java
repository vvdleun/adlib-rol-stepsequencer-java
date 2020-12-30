package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import static nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.ArgumentParseUtils.checkArgumentCount;
import static nl.vincentvanderleun.adlib.rol.stepsequencer.util.ParseUtils.parseDuration;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;

public class FadeOutFunction extends CompilableTrackFunction {
	private static final String FUNCTION_NAME = "fade-out";
	
	public FadeOutFunction() {
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
		
		track.getChannels().forEach(channel -> createFadeOut(channel, tick, duration));
	}
	
	private void createFadeOut(Channel channel, int startTick, int duration) {
//		final int endTick = startTick + duration - 1;
//		final float fromVolume = channel.getEventsAtOrBeforeTick(startTick).getVolume();
//		final float step = fromVolume / duration;
//		
//		float nextVolume = fromVolume;
//		for(int tick = startTick; tick < endTick; tick++) {
//			channel.addEvent(tick, new VolumeMultiplierEvent(nextVolume));
//			nextVolume -= step;
//			if(nextVolume < 0.0f) {
//				nextVolume = 0.0f;
//			}
//		}
//		channel.addEvent(endTick, new VolumeMultiplierEvent(0.0f));
	}
}

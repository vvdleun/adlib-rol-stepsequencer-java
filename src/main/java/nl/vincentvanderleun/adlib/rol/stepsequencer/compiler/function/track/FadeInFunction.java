package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;

public class FadeInFunction extends CompilableTrackFunction {
	private static final String FUNCTION_NAME = "fade-in";
	
	public FadeInFunction() {
		super(FUNCTION_NAME);
	}

	@Override
	public void execute(Track track, int tick, List<Object> arguments) throws CompileException {
		final int duration = (Integer)arguments.get(0);
		for(Channel channel : track.getChannels()) {
			createFadeOutOn(channel, tick, duration);
		}
	}
	
	private void createFadeOutOn(Channel channel, int startTick, int duration) {
		final int endTick = startTick + duration - 1;
		final float toVolume = channel.getEventsAtOrBeforeTick(endTick).getVolume();
		final float step = toVolume / duration;
		
		float nextVolume = 0.0f;
		for(int tick = startTick; tick < endTick; tick++) {
			channel.addEvent(tick, new VolumeMultiplierEvent(nextVolume));
			nextVolume += step;
		}
		channel.addEvent(endTick, new VolumeMultiplierEvent(toVolume));
	}
}

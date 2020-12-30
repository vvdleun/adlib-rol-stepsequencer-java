package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import java.util.Arrays;
import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.ChannelEvents;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;

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

		createFadeIn(track, tick, duration);
	}
	
	private void createFadeIn(Track track, int startTick, int duration) {
		final int endTick = startTick + duration - 1;
		
		final float[] originalChannelVolumes = getVolumeForAllChannelsAtTick(track, endTick);
		
		for(int tick = startTick; tick <= endTick; tick++) {
			final Patch currentPatch = track.getActivePatchAtTick(tick);

			for(int channelIndex = 0; channelIndex < track.getChannels().size(); channelIndex++) {
				if(channelIndex >= currentPatch.getVoices().size()) {
					break;
				}

				Channel channel = track.getChannels().get(channelIndex);
				Voice voice = currentPatch.getVoices().get(channelIndex);

				float value = (voice.getVolume() / 100f) * (tick / (endTick / 100.0f));
				
				channel.addEvent(tick, new VolumeMultiplierEvent(value));
			}
		}

		// Reset volumes after the end-tick as they were before the fade in.
		// Do this for *all* channels, otherwise channels may be silent if a switch takes place 
		// with more voices than the last voice in the fade-in range.
		for(int channelIndex = 0; channelIndex < track.getChannels().size(); channelIndex++) {
			Channel channel = track.getChannels().get(channelIndex);
			float value = originalChannelVolumes[channelIndex];
			channel.addEvent(endTick + 1, new VolumeMultiplierEvent(value));
		}
	}
	
	private float[] getVolumeForAllChannelsAtTick(Track track, int tick) {
		float[] values = new float[track.getChannels().size()];
		for(int i = 0; i < track.getChannels().size(); i++) {
			ChannelEvents channelEvents = track.getChannels().get(i).getEventsAtOrBeforeTick(tick);
			values[i] = channelEvents.getVolume();
		}
		return values;
	}
}

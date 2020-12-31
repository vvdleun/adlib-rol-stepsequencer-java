package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.ChannelEvents;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;

public class FadeInFunction extends FadeBase {
	private static final String FUNCTION_NAME = "fade-in";
	
	public FadeInFunction() {
		super(FUNCTION_NAME);
	}

	@Override
	protected void createFadeEffect(Track track, int startTick, int endTick) {
		final float[] originalChannelVolumes = getVolumeForAllChannelsAtTick(track, endTick + 1);

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
		// Do this for *all* channels, otherwise subsequent voices may have unexpected volume
		Patch lastPatch = track.getActivePatchAtTick(endTick);
		for(int channelIndex = 0; channelIndex < track.getChannels().size(); channelIndex++) {
			Channel channel = track.getChannels().get(channelIndex);

			float value;
			if(channelIndex < lastPatch.getVoices().size()) {
				value = lastPatch.getVoices().get(channelIndex).getVolume();
			} else {
				value = originalChannelVolumes[channelIndex];
			}
			channel.addEvent(endTick + 1, new VolumeMultiplierEvent(value));
		}
		
	}
	
	protected float[] getVolumeForAllChannelsAtTick(Track track, int tick) {
		float[] values = new float[track.getChannels().size()];
		for(int i = 0; i < track.getChannels().size(); i++) {
			ChannelEvents channelEvents = track.getChannels().get(i).getEventsAtOrBeforeTick(tick);
			values[i] = channelEvents.getVolume();
		}
		return values;
	}
}

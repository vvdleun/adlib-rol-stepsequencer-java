package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.ChannelEvents;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;

public class FadeInFunction extends FadeBase {
	private static final String FUNCTION_NAME = "fade-in";
	
	public FadeInFunction() {
		super(FUNCTION_NAME);
	}

	@Override
	protected void createFadeEffect(Track track, int startTick, int endTick, int numberEvents) {
		float[] originalChannelVolumes = getVolumeForAllChannelsAtTick(track, endTick);
		
		for(int channelIndex = 0; channelIndex < track.getChannels().size(); channelIndex++) {
			Channel channel = track.getChannels().get(channelIndex);
			
			final Patch finalPatch = track.getActivePatchAtTick(endTick);
			
			final float toVolume;
			if(channelIndex < finalPatch.getVoices().size()) {
				toVolume = finalPatch.getVoices().get(channelIndex).getVolume();
			} else {
				toVolume = originalChannelVolumes[channelIndex];
			}
			
			final float step = toVolume / numberEvents;

			float nextValue = 0.0f;
			for(int tick = startTick; tick <= endTick; tick++) {
				final Patch currentPatch = track.getActivePatchAtTick(tick);
				nextValue += step;
				if(channelIndex >= currentPatch.getVoices().size()) {
					break;
				}

				if(nextValue > toVolume) {
					nextValue = toVolume;
				}

				channel.addEvent(tick, new VolumeMultiplierEvent(nextValue));
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

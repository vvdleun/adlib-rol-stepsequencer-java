package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;

public class FadeOutFunction extends FadeBase {
	private static final String FUNCTION_NAME = "fade-out";
	
	public FadeOutFunction() {
		super(FUNCTION_NAME);
	}

	protected void createFadeEffect(Track track, int startTick, int endTick) {
		for(int channelIndex = 0; channelIndex < track.getChannels().size(); channelIndex++) {
			Channel channel = track.getChannels().get(channelIndex);
		
			final float fromVolume = channel.getEventsAtOrBeforeTick(startTick).getVolume();
			final float step = fromVolume / (endTick - startTick - 1);
			
			float nextVolume = fromVolume;
		
			for(int tick = startTick; tick < endTick; tick++) {
				final Patch currentPatch = track.getActivePatchAtTick(tick);

				if(channelIndex >= currentPatch.getVoices().size()) {
					break;
				}
				
				channel.addEvent(tick, new VolumeMultiplierEvent(nextVolume));
				nextVolume -= step;
				if(nextVolume < 0.0f) {
					nextVolume = 0.0f;
				}
			}
			
			// Reset all channels to 0 for consistency
			channel.addEvent(endTick + 1, new VolumeMultiplierEvent(0.0f));
		}
	}

	
}

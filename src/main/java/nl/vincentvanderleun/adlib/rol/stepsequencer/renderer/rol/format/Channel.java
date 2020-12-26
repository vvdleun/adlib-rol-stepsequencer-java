package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track.PitchTrack;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track.TimbreTrack;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track.VoiceTrack;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track.VolumeTrack;

public class Channel {
	private final VoiceTrack voiceTrack;
	private final TimbreTrack timbreTrack;
	private final VolumeTrack volumeTrack;
	private final PitchTrack pitchTrack;
	
	public Channel(VoiceTrack voiceTrack, TimbreTrack timbreTrack, VolumeTrack volumeTrack, PitchTrack pitchTrack) {
		this.voiceTrack = voiceTrack;
		this.timbreTrack = timbreTrack;
		this.volumeTrack = volumeTrack;
		this.pitchTrack = pitchTrack;
	}

	public VoiceTrack getVoiceTrack() {
		return voiceTrack;
	}
	
	public TimbreTrack getTimbreTrack() {
		return timbreTrack;
	}
	
	public VolumeTrack getVolumeTrack() {
		return volumeTrack;
	}
	
	public PitchTrack getPitchTrack() {
		return pitchTrack;
	}
}

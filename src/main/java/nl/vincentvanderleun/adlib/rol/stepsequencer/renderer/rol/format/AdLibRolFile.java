package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track.TempoTrack;

public class AdLibRolFile {
	static final int VERSION_MAJOR = 0;
	static final int VERSION_MINOR = 4;
	
	private final Header header;
	private final TempoTrack tempoTrack;
	private final Channel[] channels;
	
	public AdLibRolFile(Header header, TempoTrack tempoTrack, Channel[] channels) {
		this.header = header;
		this.tempoTrack = tempoTrack;
		this.channels = channels;
	}

	public Header getHeader() {
		return header;
	}
	
	public TempoTrack getTempoTrack() {
		return tempoTrack;
	}
	
	public Channel[] getChannels() {
		return channels;
	}
	
}

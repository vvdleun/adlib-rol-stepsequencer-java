package nl.vincentvanderleun.adlib.rol.stepsequencer.model;

/**
 * Represents supported values for the "song mode", which in turn correspondents with the two
 * most well known Yamaha OPL2 FM chip song modes.
 *
 * @author Vincent
 */
public enum SongMode {
	PERCUSSIVE(11),
	MELODIC(9);

	private int channels;
	
	SongMode(int channels) {
		this.channels = channels;
	}
	
	public int getChannels() {
		return channels;
	}
}

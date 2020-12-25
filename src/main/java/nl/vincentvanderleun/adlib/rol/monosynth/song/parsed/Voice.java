package nl.vincentvanderleun.adlib.rol.monosynth.song.parsed;

/**
 * Represents a parsed voice, parsed while parsing [PATCH] blocks, from the input file.
 *
 * @author Vincent
 */
public class Voice {
	private String name;
	private String instrument;
	private float pitch;
	private int transpose;
	private float volume;
	private int channel;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public int getTranspose() {
		return transpose;
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
}

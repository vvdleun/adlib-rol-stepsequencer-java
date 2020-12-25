package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * Overrules the current patch' voice's volumes, while trying to keep the voice's volume offset ratio in mind (if possible).
 * Softer voices remain softer.
 *
 * @author Vincent
 */
public class Volume extends Event {
	private final float volume;
	
	public Volume(float volume) {
		super(EventType.VOLUME);

		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}

	@Override
	public String toString() {
		return "Volume [volume=" + volume + "]";
	}
}

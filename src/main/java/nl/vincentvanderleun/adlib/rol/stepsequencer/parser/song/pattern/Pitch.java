package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.pattern;

/**
 * Overrules the current patch' voice's pitches, while trying to keep the voice's pitch offset ratio in mind (if possible).
 *
 * @author Vincent
 */
public class Pitch extends Event {
	private final float pitch;
	
	public Pitch(float pitch) {
		super(EventType.PITCH);

		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		return "Pitch [pitch=" + pitch + "]";
	}
}

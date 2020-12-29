package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * Overrules the current patch' voice's pitches, while trying to keep the voice's pitch offset ratio in mind (if possible).
 *
 * @author Vincent
 */
public class Pitch extends Event {
	private final float pitch;
	private final int duration;
	
	public Pitch(float pitch, int duration) {
		super(EventType.PITCH);

		this.pitch = pitch;
		this.duration = duration;
	}

	public float getPitch() {
		return pitch;
	}
	
	public int getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return "Pitch [pitch=" + pitch + "]";
	}
}

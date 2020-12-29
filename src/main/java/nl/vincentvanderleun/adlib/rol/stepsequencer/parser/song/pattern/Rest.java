package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * Represents a "rest": a tick of silence.
 *
 * @author Vincent
 */
public class Rest extends Event {
	private final int duration;
	
	public Rest(int duration) {
		super(EventType.REST);
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}
	
	@Override
	public String toString() {
		return "Rest [duration=" + duration + "]";
	}
}

package nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.pattern;

/**
 * Represents a "rest": a tick of silence.
 *
 * @author Vincent
 */
public class Rest extends Event {

	public Rest() {
		super(EventType.REST);
	}

	@Override
	public String toString() {
		return "Rest []";
	}
}

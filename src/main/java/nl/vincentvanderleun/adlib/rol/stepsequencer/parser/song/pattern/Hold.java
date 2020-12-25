package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.pattern;

/**
 * The "Hold" event increases the duration of the previous note with 1 tick.
 * If there is no previous note, a rest will be added instead.
 *
 * @author Vincent
 */
public class Hold extends Event {
	public Hold() {
		super(EventType.HOLD);
	}

	@Override
	public String toString() {
		return "Hold []";
	}
}

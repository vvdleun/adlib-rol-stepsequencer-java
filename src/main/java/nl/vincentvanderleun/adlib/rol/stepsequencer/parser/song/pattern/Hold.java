package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * The "Hold" event increases the duration of the previous note with 1 tick.
 * If there is no previous note, a rest will be added instead.
 *
 * @author Vincent
 */
public class Hold extends Event {
	final int duration;
	
	public Hold(int duration) {
		super(EventType.HOLD);
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}
	
	@Override
	public String toString() {
		return "Hold [duration=" + duration + "]";
	}
}

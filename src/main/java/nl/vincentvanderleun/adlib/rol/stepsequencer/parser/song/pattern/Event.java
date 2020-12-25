package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

/**
 * Forms the basis for each individual event that a parsed pattern can contain.
 *
 * @author Vincent
 */
public abstract class Event {
	private final EventType eventType;
	
	public Event(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}

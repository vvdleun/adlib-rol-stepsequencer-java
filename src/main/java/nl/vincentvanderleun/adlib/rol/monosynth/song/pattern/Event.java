package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern;

public abstract class Event {
	private final EventType eventType;
	
	public Event(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}

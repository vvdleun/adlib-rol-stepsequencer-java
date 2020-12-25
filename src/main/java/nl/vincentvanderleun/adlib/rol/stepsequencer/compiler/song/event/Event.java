package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

public abstract class Event {
	private final EventType eventType;
	
	public Event(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}

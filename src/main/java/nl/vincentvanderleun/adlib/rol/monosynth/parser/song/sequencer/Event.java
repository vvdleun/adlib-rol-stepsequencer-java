package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.sequencer;

public abstract class Event {
	protected final EventType eventType;
	
	public Event(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}
}
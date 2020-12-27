package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer;

import java.util.Objects;

public abstract class Event {
	protected final EventType eventType;
	
	public Event(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return eventType == other.eventType;
	}

	@Override
	public String toString() {
		return "Event [eventType=" + eventType + "]";
	}
}

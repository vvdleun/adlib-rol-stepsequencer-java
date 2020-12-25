package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

import java.util.List;

/**
 * Contains all events that make up a single pattern.
 *
 * @author Vincent
 */
public class Pattern {
	private String name;
	private List<Event> events;

	public Pattern() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Event> getEvents() {
		return events;
	}
	
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}

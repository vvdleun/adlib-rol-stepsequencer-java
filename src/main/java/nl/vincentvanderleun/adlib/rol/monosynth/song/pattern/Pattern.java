package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern;

import java.util.List;

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

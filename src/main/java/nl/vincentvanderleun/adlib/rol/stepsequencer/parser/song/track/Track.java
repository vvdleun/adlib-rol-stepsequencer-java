package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track;

import java.util.List;

public class Track {
	private List<Event> events;
	
	public Track() {
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}

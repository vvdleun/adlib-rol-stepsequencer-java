package nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer;

import java.util.List;

public class Sequencer {
	private List<Event> events;
	
	public Sequencer() {
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	public void setEvents(List<Event> events) {
		this.events = events;
	}
}

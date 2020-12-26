package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

abstract class DefaultEventTrack<T> {
	protected final String trackName;
	protected final List<T> events;
	
	public DefaultEventTrack(String trackName, List<T> events) {
		this.trackName = trackName;
		this.events = events;
	}

	public String getTrackName() {
		return trackName;
	}

	public List<T> getEvents() {
		return events;
	}
}

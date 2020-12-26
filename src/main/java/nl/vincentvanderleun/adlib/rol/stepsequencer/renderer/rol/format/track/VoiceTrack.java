package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Note;

public class VoiceTrack {
	private final String trackName;
	private int totalTicks;
	private final List<Note> notes;
	
	public VoiceTrack(String trackName, int totalTicks, List<Note> notes) {
		this.trackName = trackName;
		this.totalTicks = totalTicks;
		this.notes = notes;
	}

	public String getTrackName() {
		return trackName;
	}

	public int getTotalTicks() {
		return totalTicks;
	}

	public List<Note> getNotes() {
		return notes;
	}
}

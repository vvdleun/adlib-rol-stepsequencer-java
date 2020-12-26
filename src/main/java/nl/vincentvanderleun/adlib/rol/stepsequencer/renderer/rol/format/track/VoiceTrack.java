package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Note;

public class VoiceTrack extends Track<Note> {
	private int totalTicks;
	
	public VoiceTrack(String trackName, int totalTicks, List<Note> notes) {
		super(trackName, notes);
		this.totalTicks = totalTicks;
	}

	public int getTotalTicks() {
		return totalTicks;
	}
}

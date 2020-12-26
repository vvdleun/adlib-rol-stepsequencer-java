package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event;

public class Note {
	private final int note;
	private final int duration;
	
	public Note(int note, int duration) {
		this.note = note;
		this.duration = duration;
	}

	public int getNote() {
		return note;
	}

	public int getDuration() {
		return duration;
	}
}

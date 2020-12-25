package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

import java.util.Objects;

public class NoteEvent extends Event {
	private int note;
	private int duration;
	
	public NoteEvent(int note, int duration) {
		super(EventType.NOTE);
		this.note = note;
		this.duration = duration;
	}

	public int getNote() {
		return note;
	}
	
	public void setNote(int note) {
		this.note = note;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(duration, note);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NoteEvent other = (NoteEvent) obj;
		return duration == other.duration && note == other.note;
	}

	@Override
	public String toString() {
		return "RolNoteEvent [note=" + note + ", duration=" + duration + "]";
	}
}

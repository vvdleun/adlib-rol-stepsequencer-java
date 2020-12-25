package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

import java.util.Objects;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.NoteValue;

public class NoteEvent extends Event {
	private NoteValue note;
	private int duration;
	private int octave;
	private int transpose;
	
	public NoteEvent(NoteValue note, int duration, int octave, int transpose) {
		super(EventType.NOTE);
		this.note = note;
		this.duration = duration;
		this.octave = octave;
		this.transpose = transpose;
	}

	public NoteValue getNote() {
		return note;
	}
	
	public void setNote(NoteValue note) {
		this.note = note;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getOctave() {
		return octave;
	}
	
	public void setOctave(int octave) {
		this.octave = octave;
	}
	
	public int getTranspose() {
		return transpose;
	}
	
	public void setTranspose(int transpose) {
		this.transpose = transpose;
	}

	@Override
	public int hashCode() {
		return Objects.hash(duration, note, octave, transpose);
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
		return duration == other.duration && note == other.note && octave == other.octave
				&& transpose == other.transpose;
	}

	@Override
	public String toString() {
		return "NoteEvent [note=" + note + ", duration=" + duration + ", octave=" + octave + ", transpose=" + transpose
				+ "]";
	}
}

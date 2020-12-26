package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Note;

/**
 * The "NoteEvent" event represents a note and duration. An individual
 * note can also overrule the current octave if it specifies an octave offset.
 *
 * @author Vincent
 */
public class NoteEvent extends Event {
	private final Note note;
	private final int octaveOffset;
	// Sometimes you have to be practical in life...
	private int duration;

	public NoteEvent(Note note, int duration, int octaveOffset) {
		super(EventType.NOTE);

		this.note = note;
		this.duration = duration;
		this.octaveOffset = octaveOffset;
	}

	public Note getNote() {
		return note;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getOctaveOffset() {
		return octaveOffset;
	}
	
	public void increaseDuration(int ticks) {
		this.duration += ticks;
	}

	@Override
	public String toString() {
		return "NoteEvent [note=" + note + ", duration=" + duration + ", octaveOffset=" + octaveOffset + "]";
	}
}

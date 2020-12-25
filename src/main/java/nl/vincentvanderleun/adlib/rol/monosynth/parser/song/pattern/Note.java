package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.pattern;

/**
 * The "Note" event represents a note and duration. An individual
 * note can also overrule the current octave if it specifies an octave offset.
 *
 * @author Vincent
 */
public class Note extends Event {
	private final NoteValue note;
	private final int duration;
	private final int octaveOffset;
	
	public Note(NoteValue note, int duration, int octaveOffset) {
		super(EventType.NOTE);

		this.note = note;
		this.duration = duration;
		this.octaveOffset = octaveOffset;
	}

	public NoteValue getNote() {
		return note;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getOctaveOffset() {
		return octaveOffset;
	}

	@Override
	public String toString() {
		return "Note [note=" + note + ", duration=" + duration + ", octaveOffset=" + octaveOffset + "]";
	}
}

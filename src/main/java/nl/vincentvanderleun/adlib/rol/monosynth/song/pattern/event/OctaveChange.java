package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class OctaveChange extends Event {
	private final int octave;
	
	public OctaveChange(int octave) {
		super(EventType.OCTAVE);
		this.octave = octave;
	}
	
	public int getOctave() {
		return octave;
	}

	@Override
	public String toString() {
		return "Octave [octave=" + octave + "]";
	}
}

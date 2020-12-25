package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.pattern;

/**
 * Changes the current octave. The following notes (which do not overrule the current
 * octave) will use this octave.
 * 
 * Note that individual voices can overrule this with their "transpose" setting.
 *
 * @author Vincent
 */
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
package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song;

/**
 * Represents a parsed [SONG] header block from the input file.
 *
 * @author Vincent
 */
public class SongHeader {
	public static final int DEFAULT_VERSION = 1;
	
	private final int version;

	private Target target;
	private float tempo;
	private int ticksPerBeat;
	private int beatsPerMeasure;
	private SongMode mode;
	
	public SongHeader() {
		this(DEFAULT_VERSION);
	}

	private SongHeader(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public float getTempo() {
		return tempo;
	}

	public void setTempo(float tempo) {
		this.tempo = tempo;
	}

	public int getTicksPerBeat() {
		return ticksPerBeat;
	}

	public void setTicksPerBeat(int ticksPerBeat) {
		this.ticksPerBeat = ticksPerBeat;
	}

	public int getBeatsPerMeasure() {
		return beatsPerMeasure;
	}

	public void setBeatsPerMeasure(int beatsPerMeasure) {
		this.beatsPerMeasure = beatsPerMeasure;
	}

	public SongMode getMode() {
		return mode;
	}

	public void setMode(SongMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "SongHeader [version=" + version + ", target=" + target + ", tempo=" + tempo + ", ticksPerBeat="
				+ ticksPerBeat + ", beatsPerMeasure=" + beatsPerMeasure + ", mode=" + mode + "]";
	}
}

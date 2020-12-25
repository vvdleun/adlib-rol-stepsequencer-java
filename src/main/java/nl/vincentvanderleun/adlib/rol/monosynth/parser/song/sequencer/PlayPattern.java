package nl.vincentvanderleun.adlib.rol.monosynth.parser.song.sequencer;

public class PlayPattern extends Event {
	private final String patternName;
	
	public PlayPattern(String patternName) {
		super(EventType.PLAY_PATTERN);

		this.patternName = patternName;
	}
	
	public String getPatternName() {
		return patternName;
	}
}

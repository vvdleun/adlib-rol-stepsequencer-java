package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track;

public class PlayPattern extends Event {
	private final String patternName;
	private final int times;
	
	public PlayPattern(String patternName, int times) {
		super(EventType.PLAY_PATTERN);

		this.patternName = patternName;
		this.times = times;
	}
	
	public String getPatternName() {
		return patternName;
	}
	
	public int getTimes() {
		return times;
	}
}

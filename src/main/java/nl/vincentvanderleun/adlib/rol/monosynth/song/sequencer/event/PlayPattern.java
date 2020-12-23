package nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.EventType;

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

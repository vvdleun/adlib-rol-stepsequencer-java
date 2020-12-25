package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Sequencer;

/**
 * Parsed song representation.
 *
 * This class follows the structure of the input file closely.
 *
 * Functions used in the input file have been parsed, but not been applied at this stage. Also
 * not any form of normalization of events have taken place.
 *
 * @author Vincent
 */
public class Song {
	private SongHeader header;
	private List<Patch> patches;
	private List<Pattern> patterns;
	private Sequencer sequencer;

	public SongHeader getHeader() {
		return header;
	}

	public void setHeader(SongHeader header) {
		this.header = header;
	}

	public List<Patch> getPatches() {
		return patches;
	}

	public void setPatches(List<Patch> patches) {
		this.patches = patches;
	}
	
	public List<Pattern> getPatterns() {
		return patterns;
	}
	
	public void setPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
	}
	
	public Sequencer getSequencer() {
		return sequencer;
	}
	
	public void setSequencer(Sequencer sequencer) {
		this.sequencer = sequencer;
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Track;

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
public class ParsedSong {
	private SongHeader header;
	private List<Patch> patches;
	private List<Pattern> patterns;
	private Track track;

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
	
	public Track getTrack() {
		return track;
	}
	
	public void setTrack(Track track) {
		this.track = track;
	}
}

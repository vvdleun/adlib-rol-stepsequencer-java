package nl.vincentvanderleun.adlib.rol.monosynth.song;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Sequencer;

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

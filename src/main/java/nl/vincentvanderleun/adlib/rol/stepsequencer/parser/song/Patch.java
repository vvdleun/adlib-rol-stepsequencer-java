package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song;

import java.util.List;

/**
 * Represents a parsed [PATCH <patchname>] block from the input file.
 *
 * @author Vincent
 */
public class Patch {
	private String name;
	private List<Voice> voices;
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Voice> getVoices() {
		return voices;
	}

	public void setVoices(List<Voice> voices) {
		this.voices = voices;
	}
}

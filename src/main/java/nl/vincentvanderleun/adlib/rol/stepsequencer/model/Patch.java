package nl.vincentvanderleun.adlib.rol.stepsequencer.model;

import java.util.List;

/**
 * Represents a high-level patch, which consists of a collection of individual mono voices.
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

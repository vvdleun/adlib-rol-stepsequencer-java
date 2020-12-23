package nl.vincentvanderleun.adlib.rol.monosynth.song;

import java.util.List;

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

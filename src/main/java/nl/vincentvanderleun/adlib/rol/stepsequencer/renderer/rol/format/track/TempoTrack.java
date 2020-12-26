package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Tempo;

public class TempoTrack {
	private final String trackName;
	private final float tempo;
	private final List<Tempo> tempoMultiplierEvents;
	
	public TempoTrack(String trackName, float tempo, List<Tempo> tempoMultipliers) {
		this.trackName = trackName;
		this.tempo = tempo;
		this.tempoMultiplierEvents = tempoMultipliers;
	}

	public String getTrackName() {
		return trackName;
	}

	public float getTempo() {
		return tempo;
	}

	public List<Tempo> getTempoMultiplierEvents() {
		return tempoMultiplierEvents;
	}
}

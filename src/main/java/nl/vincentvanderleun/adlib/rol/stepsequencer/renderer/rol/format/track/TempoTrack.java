package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Tempo;

public class TempoTrack extends Track<Tempo> {
	private final float tempo;
	
	public TempoTrack(String trackName, float tempo, List<Tempo> tempoMultipliers) {
		super(trackName, tempoMultipliers);
		this.tempo = tempo;
	}

	public float getTempo() {
		return tempo;
	}
}

package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.event;

import java.util.ArrayList;
import java.util.List;

public class Tracks {
	public final NormalizedEvents<Float> tempoEvents;
	public final List<Channel> channels;
	
	public Tracks() {
		this.tempoEvents = new NormalizedEvents<>();
		this.channels = new ArrayList<>(11);
		
		for(int i = 0; i < 11; i++) {
			channels.add(new Channel(i));
		}
	}

	public void addTempoEvent(int tick, float tempoMultiplier) {
		tempoEvents.add(tick, tempoMultiplier);
	}

	public List<Channel> getChannels() {
		return channels;
	}
}

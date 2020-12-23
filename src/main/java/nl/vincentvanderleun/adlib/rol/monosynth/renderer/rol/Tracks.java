package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Tracks {
	public final Map<Integer, Float> tempoEvents;
	public final List<Channel> channels;
	
	public Tracks() {
		this.tempoEvents = new TreeMap<>();
		this.channels = new ArrayList<>(11);
		
		for(int i = 0; i < 11; i++) {
			channels.add(new Channel());
		}
	}

	public Map<Integer, Float> getTempoEvents() {
		return tempoEvents;
	}

	public List<Channel> getChannels() {
		return channels;
	}
}

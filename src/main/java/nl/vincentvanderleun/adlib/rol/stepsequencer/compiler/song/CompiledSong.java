package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Target;

public class CompiledSong {
	private static final int MAX_CHANNELS = 11;
	
	private final Target target;
	private final float tempo;
	private final int ticksPerBeat;
	private final int beatsPerMeasure;
	private final SongMode songMode;

	public final NormalizedEventMap<Float> tempoEvents;
	public final List<Channel> channels;
	
	public CompiledSong(Target target, float tempo, int ticksPerBeat, int beatsPerMeasure, SongMode songMode) {
		this.target = target;
		this.tempo = tempo;
		this.ticksPerBeat = ticksPerBeat;
		this.beatsPerMeasure = beatsPerMeasure;
		this.songMode = songMode;
		
		tempoEvents = new NormalizedEventMap<>();
		
		this.channels = new ArrayList<>(MAX_CHANNELS);
		for(int i = 0; i < 11; i++) {
			channels.add(new Channel(i));
		}
	}

	public Target getTarget() {
		return target;
	}

	public float getTempo() {
		return tempo;
	}

	public int getTicksPerBeat() {
		return ticksPerBeat;
	}

	public int getBeatsPerMeasure() {
		return beatsPerMeasure;
	}

	public SongMode getSongMode() {
		return songMode;
	}

	public SortedMap<Integer, Float> getTempoEvents() {
		return tempoEvents.getMap();
	}

	public List<Channel> getChannels() {
		return channels;
	}
}

package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class Volume extends Event {
	private final float volume;
	
	public Volume(float volume) {
		super(EventType.VOLUME);

		this.volume = volume;
	}

	public float getVolume() {
		return volume;
	}

	@Override
	public String toString() {
		return "Volume [volume=" + volume + "]";
	}
}

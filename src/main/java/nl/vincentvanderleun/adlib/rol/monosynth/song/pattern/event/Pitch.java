package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class Pitch extends Event {
	private final float pitch;
	
	public Pitch(float pitch) {
		super(EventType.PITCH);

		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		return "Pitch [pitch=" + pitch + "]";
	}
}

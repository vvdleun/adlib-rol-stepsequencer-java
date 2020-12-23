package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class Rest extends Event {

	public Rest() {
		super(EventType.REST);
	}

	@Override
	public String toString() {
		return "Rest []";
	}
}

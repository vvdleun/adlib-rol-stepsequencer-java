package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class Hold extends Event {

	public Hold() {
		super(EventType.HOLD);
	}

	@Override
	public String toString() {
		return "Hold []";
	}
}

package nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event;

import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.EventType;

public class PatchChange extends Event {
	private final String patchName;

	public PatchChange(String patchName) {
		super(EventType.PATCH);
		this.patchName = patchName;
	}

	public String getPatchName() {
		return patchName;
	}

	@Override
	public String toString() {
		return "Patch [patchName=" + patchName + "]";
	}
}

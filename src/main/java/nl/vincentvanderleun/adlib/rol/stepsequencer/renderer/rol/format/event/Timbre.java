package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event;

public class Timbre {
	private final int tick;
	private final String timbre;
	private final int unknown;
	
	public Timbre(int tick, String timbre, int unknown) {
		this.tick = tick;
		this.timbre = timbre;
		this.unknown = unknown;
	}

	public int getTick() {
		return tick;
	}

	public String getTimbre() {
		return timbre;
	}

	public int getUnknown() {
		return unknown;
	}
}

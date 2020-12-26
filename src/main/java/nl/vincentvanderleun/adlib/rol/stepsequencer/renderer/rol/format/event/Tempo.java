package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event;

public class Tempo {
	private final int tick;
	private final float tempoMultiplier;
	
	public Tempo(int tick, float tempoMultiplier) {
		this.tick = tick;
		this.tempoMultiplier = tempoMultiplier;
	}

	public int getTick() {
		return tick;
	}

	public float getTempoMultiplier() {
		return tempoMultiplier;
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event;

public class FloatBasedEvent {
	private final int tick;
	private final float value;
	
	public FloatBasedEvent(int tick, float value) {
		this.tick = tick;
		this.value = value;
	}

	public int getTick() {
		return tick;
	}

	public float getValue() {
		return value;
	}
}

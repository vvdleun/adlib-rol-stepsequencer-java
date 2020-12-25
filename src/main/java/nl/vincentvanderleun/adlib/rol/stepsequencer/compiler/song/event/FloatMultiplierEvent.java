package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

abstract class FloatMultiplierEvent extends Event {
	protected float value;
	
	public FloatMultiplierEvent(EventType eventType, float value) {
		super(eventType);
		this.value = value;
	}
	
	public float getMulitplier() {
		return value;
	}
	
	protected int compareTo(float other) {
		if(value < other) {
			return -1;
		} else if (value > other) {
			return 1;
		}
		return 0;
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

public class PitchMultiplierEvent extends FloatMultiplierEvent implements Comparable<PitchMultiplierEvent> {

	public PitchMultiplierEvent(float value) {
		super(EventType.PITCH, value);
	}

	@Override
	public int compareTo(PitchMultiplierEvent o) {
		return this.compareTo(o.getMulitplier());
	}
}

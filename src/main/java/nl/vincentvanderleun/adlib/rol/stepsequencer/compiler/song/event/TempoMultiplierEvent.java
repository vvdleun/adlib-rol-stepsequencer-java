package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

public class TempoMultiplierEvent extends FloatMultiplierEvent implements Comparable<PitchMultiplierEvent> {
	public TempoMultiplierEvent(float value) {
		super(EventType.TEMPO, value);
	}

	@Override
	public int compareTo(PitchMultiplierEvent o) {
		return this.compareTo(o.getMulitplier());
	}
}

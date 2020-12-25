package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

public class VolumeMultiplierEvent extends FloatMultiplierEvent implements Comparable<VolumeMultiplierEvent> {
	public VolumeMultiplierEvent(float value) {
		super(EventType.VOLUME, value);
	}

	@Override
	public int compareTo(VolumeMultiplierEvent o) {
		return this.compareTo(o.getMulitplier());
	}
}

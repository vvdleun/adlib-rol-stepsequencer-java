package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.Objects;

public class TickChannelEvents implements Comparable<TickChannelEvents> {
	private final int tick;
	private final NoteEvent note;
	private final String instrument;
	private final Float volume;
	private final Float pitch;

	TickChannelEvents(int tick, NoteEvent noteEvent, String instrument, Float volume, Float pitch) {
		this.tick = tick;
		this.note = noteEvent;
		this.instrument = instrument;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public int compareTo(TickChannelEvents other) {
		if(tick > other.getTick()) {
			return 1;
		} else if(tick < other.getTick()) {
			return -1;
		}
		return 0;
	}

	public int getTick() {
		return tick;
	}

	public NoteEvent getNote() {
		return note;
	}

	public String getInstrument() {
		return instrument;
	}

	public Float getVolume() {
		return volume;
	}

	public Float getPitch() {
		return pitch;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instrument, note, pitch, tick, volume);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TickChannelEvents other = (TickChannelEvents) obj;
		return Objects.equals(instrument, other.instrument) && Objects.equals(note, other.note)
				&& Objects.equals(pitch, other.pitch) && tick == other.tick && Objects.equals(volume, other.volume);
	}

	@Override
	public String toString() {
		return "TickChannelEvents [tick=" + tick + ", note=" + note + ", instrument=" + instrument + ", volume="
				+ volume + ", pitch=" + pitch + "]";
	}
}

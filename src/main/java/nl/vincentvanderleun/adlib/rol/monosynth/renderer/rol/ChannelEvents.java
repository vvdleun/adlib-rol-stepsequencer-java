package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.Objects;

public class ChannelEvents implements Comparable<ChannelEvents> {
	private final int tick;
	private final NoteEvent note;
	private final String instrument;
	private final Float volume;
	private final Float pitch;

	public static ChannelEvents from(int tick, NoteEvent noteEvent, String instrument, Float volume, Float pitch) {
		return new ChannelEvents(tick, noteEvent, instrument, volume, pitch);
	}
	
	public static ChannelEvents fromNoteEvenOnly(int tick, NoteEvent noteEvent) {
		return new ChannelEvents(
				tick,						// Tick
				noteEvent,					// Note event
				null,						// Instrument
				null,						// Volume
				null);						// Pitch
	}

	public static ChannelEvents fromInstrumentOnly(int tick, String instrument) {
		return new ChannelEvents(
				tick,						// Tick
				null,						// Note event
				instrument,					// Instrument
				null,						// Volume
				null);						// Pitch		
	}

	public static ChannelEvents fromVolumeOnly(int tick, float volume) {
		return new ChannelEvents(
				tick,						// Tick
				null,						// Note event
				null,						// Instrument
				volume,						// Volume
				null);						// Pitch		
	}

	public static ChannelEvents fromPitchOnly(int tick, float pitch) {
		return new ChannelEvents(
				tick,						// Tick
				null,						// Note event
				null,						// Instrument
				null,						// Volume
				pitch);						// Pitch		
	}

	private ChannelEvents(int tick, NoteEvent noteEvent, String instrument, Float volume, Float pitch) {
		this.tick = tick;
		this.note = noteEvent;
		this.instrument = instrument;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public int compareTo(ChannelEvents other) {
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
		ChannelEvents other = (ChannelEvents) obj;
		return Objects.equals(instrument, other.instrument) && Objects.equals(note, other.note)
				&& Objects.equals(pitch, other.pitch) && tick == other.tick && Objects.equals(volume, other.volume);
	}

	@Override
	public String toString() {
		return "TickChannelEvents [tick=" + tick + ", note=" + note + ", instrument=" + instrument + ", volume="
				+ volume + ", pitch=" + pitch + "]";
	}
}

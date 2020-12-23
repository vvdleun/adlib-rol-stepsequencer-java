package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.Objects;

public class ChannelEvents {
	private NoteEvent note;
	private String instrument;
	private Float volume;
	private Float pitch;
	
	ChannelEvents(NoteEvent note, String instrument, Float volume, Float pitch) {
		this.note = note;
		this.instrument= instrument;
		this.volume = volume;
		this.pitch = pitch;
	}

	public static ChannelEvents empty() {
		return new ChannelEvents(null, null, null, null);
	}
	
	static ChannelEvents fromNote(NoteEvent note) {
		return new ChannelEvents(note, null, null, null);
	}

	public NoteEvent getNote() {
		return note;
	}

	public void setNote(NoteEvent note) {
		this.note = note;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Float getPitch() {
		return pitch;
	}

	public void setPitch(Float pitch) {
		this.pitch = pitch;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instrument, note, pitch, volume);
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
				&& Objects.equals(pitch, other.pitch) && Objects.equals(volume, other.volume);
	}

	@Override
	public String toString() {
		return "ChannelEvents [note=" + note + ", instrument=" + instrument + ", volume=" + volume + ", pitch=" + pitch
				+ "]";
	}
}

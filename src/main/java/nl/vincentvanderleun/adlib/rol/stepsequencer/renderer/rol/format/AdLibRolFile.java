package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format;

import java.util.List;

public class AdLibRolFile {
	static final int VERSION_MAJOR = 0;
	static final int VERSION_MINOR = 4;
	
	private final Header header;
	private final TempoTrack tempoTrack;
	private final Channel[] channels;
	
	public AdLibRolFile(Header header, TempoTrack tempoTrack, Channel[] channels) {
		this.header = header;
		this.tempoTrack = tempoTrack;
		this.channels = channels;
	}

	public Header getHeader() {
		return header;
	}
	
	public TempoTrack getTempoTrack() {
		return tempoTrack;
	}
	
	public Channel[] getChannels() {
		return channels;
	}
	
	// Global structures

	public static class Header {
		private final String signature;
		private final int ticksPerBeat;
		private final int beatsPerMeasure;
		private final int scaleY;
		private final int scaleX;
		private final int songMode;
		private final int[] counters;

		public Header(String signature, int ticksPerBeat, int beatsPerMeasure, int scaleY, int scaleX, int songMode, int[] counters) {
			this.signature = signature;
			this.ticksPerBeat = ticksPerBeat;
			this.beatsPerMeasure = beatsPerMeasure;
			this.scaleY = scaleY;
			this.scaleX = scaleX;
			this.songMode = songMode;
			this.counters = counters;
		}
		
		public String getSignature() {
			return signature;
		}

		public int getTicksPerBeat() {
			return ticksPerBeat;
		}

		public int getBeatsPerMeasure() {
			return beatsPerMeasure;
		}

		public int getScaleY() {
			return scaleY;
		}

		public int getScaleX() {
			return scaleX;
		}

		public int getSongMode() {
			return songMode;
		}

		public int[] getCounters() {
			return counters;
		}
	}

	// - events
	
	public static class Tempo {
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

	public static class Note {
		private final int note;
		private final int duration;
		
		public Note(int note, int duration) {
			this.note = note;
			this.duration = duration;
		}

		public int getNote() {
			return note;
		}

		public int getDuration() {
			return duration;
		}
	}
	
	public static class Timbre {
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
	
	private abstract static class FloatBasedEvent {
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
	
	public static class Volume extends FloatBasedEvent {
		public Volume(int tick, float volumeMultiplier) {
			super(tick, volumeMultiplier);
		}
	}

	public static class Pitch extends FloatBasedEvent {
		public Pitch(int tick, float pitchMultiplier) {
			super(tick, pitchMultiplier);
		}
	}

	// - tracks
	
	public static class TempoTrack {
		private final String trackName;
		private final float tempo;
		private final List<Tempo> tempoMultiplierEvents;
		
		public TempoTrack(String trackName, float tempo, List<Tempo> tempoMultipliers) {
			this.trackName = trackName;
			this.tempo = tempo;
			this.tempoMultiplierEvents = tempoMultipliers;
		}

		public String getTrackName() {
			return trackName;
		}

		public float getTempo() {
			return tempo;
		}

		public List<Tempo> getTempoMultiplierEvents() {
			return tempoMultiplierEvents;
		}
	}

	// - channel

	public static class Channel {
		private final VoiceTrack voiceTrack;
		private final TimbreTrack timbreTrack;
		private final VolumeTrack volumeTrack;
		private final PitchTrack pitchTrack;
		
		public Channel(VoiceTrack voiceTrack, TimbreTrack timbreTrack, VolumeTrack volumeTrack, PitchTrack pitchTrack) {
			this.voiceTrack = voiceTrack;
			this.timbreTrack = timbreTrack;
			this.volumeTrack = volumeTrack;
			this.pitchTrack = pitchTrack;
		}

		public VoiceTrack getVoiceTrack() {
			return voiceTrack;
		}
		
		public TimbreTrack getTimbreTrack() {
			return timbreTrack;
		}
		
		public VolumeTrack getVolumeTrack() {
			return volumeTrack;
		}
		
		public PitchTrack getPitchTrack() {
			return pitchTrack;
		}
	}

	// - tracks

	public static class VoiceTrack {
		private final String trackName;
		private int totalTicks;
		private final List<Note> notes;
		
		public VoiceTrack(String trackName, int totalTicks, List<Note> notes) {
			this.trackName = trackName;
			this.totalTicks = totalTicks;
			this.notes = notes;
		}

		public String getTrackName() {
			return trackName;
		}

		public int getTotalTicks() {
			return totalTicks;
		}

		public List<Note> getNotes() {
			return notes;
		}
	}
	
	private static abstract class DefaultEventTrack<T> {
		protected final String trackName;
		protected final List<T> events;
		
		public DefaultEventTrack(String trackName, List<T> events) {
			this.trackName = trackName;
			this.events = events;
		}

		public String getTrackName() {
			return trackName;
		}

		public List<T> getEvents() {
			return events;
		}
	}
	
	public static class TimbreTrack extends DefaultEventTrack<Timbre>  {
		public TimbreTrack(String trackName, List<Timbre> timbreEvents) {
			super(trackName, timbreEvents);
		}
	}
	
	public static class VolumeTrack extends DefaultEventTrack<Volume>  {
		public VolumeTrack(String trackName, List<Volume> volumeEvents) {
			super(trackName, volumeEvents);
		}
	}
	
	public static class PitchTrack extends DefaultEventTrack<Pitch>  {
		public PitchTrack(String trackName, List<Pitch> pitchEvents) {
			super(trackName, pitchEvents);
		}
	}
}

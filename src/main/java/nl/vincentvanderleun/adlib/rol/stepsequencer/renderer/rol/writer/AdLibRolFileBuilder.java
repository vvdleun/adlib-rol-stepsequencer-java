package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This builder (one of the most boring to write...) builds the Ad Lib ROL file using most
 * known conventions used by Ad Lib, Inc's Visual Composer software version 1.5 from 1987. 
 * 
 * Several bytes not documented in the official specifications back in the day are set with
 * values that have been found by looking at existing ROL files with a hex editor.
 * 
 * Thanks for http://www.shikadi.net/moddingwiki/ROL_Format, especially for the counter bytes.
 *
 * @author Vincent
 *
 */
public class AdLibRolFileBuilder {
	private static final int PERCUSSIVE_SONG_MODE = 0;
	private static final int MELODIC_SONG_MODE = 1;
	private static final String SIGNATURE = "\\roll\\default";
	private static final int SCALE_Y = 0x48;
	private static final int SCALE_X = 0x45;
	private static final int CHANNELS = 11;
	private static final int UNKNOWN_INSTUMENT_BYTE = 0;
	private static final String TEMPO_TRACK_NAME = "Tempo";
	private static final String VOICE_TRACK_PREFIX = "Voix ";
	private static final String TIMBRE_TRACK_PREFIX = "Timbre ";
	private static final String VOLUME_TRACK_PREFIX = "Volume ";
	private static final String PITCH_TRACK_PREFIX = "Pitch ";
	private static final float DEFAULT_TEMPO_MULTIPLIER = 1.0f;
	private static final float DEFAULT_VOLUME_MULTIPLIER = 0.75f;
	private static final float DEFAULT_PITCH_MULTIPLIER = 1.0f;
	private static final String DEFAULT_MELODIC_TIMBRE = "piano1";

	public static AdLibRolFileBuilder createRolFile() {
		return createPercussiveRolFile();
	}

	public static AdLibRolFileBuilder createMelodicRolFile() {
		return new AdLibRolFileBuilder(MELODIC_SONG_MODE);
	}

	public static AdLibRolFileBuilder createPercussiveRolFile() {
		return new AdLibRolFileBuilder(PERCUSSIVE_SONG_MODE);
	}

	private final int songMode;
	private final List<AdLibRolFile.Tempo> tempoEvents;
	private final ChannelEventLists[] channels;
	
	private int ticksPerBeat;
	private int beatsPerMeasure;
	private float tempo;
	
	private AdLibRolFileBuilder(int songMode) {
		this.songMode = songMode;
		this.tempoEvents = new LinkedList<>();
		this.channels = createChannelEventLists();
	}

	private ChannelEventLists[] createChannelEventLists() {
		ChannelEventLists[] channels = new ChannelEventLists[CHANNELS];

		for(int i = 0; i < CHANNELS; i++) {
			channels[i] = new ChannelEventLists();
			channels[i].noteEvents = new LinkedList<AdLibRolFile.Note>();
			channels[i].timbreEvents = new LinkedList<AdLibRolFile.Timbre>();
			channels[i].volumeEvents = new LinkedList<AdLibRolFile.Volume>();
			channels[i].pitchEvents = new LinkedList<AdLibRolFile.Pitch>();
		}
		
		return channels;
	}
	
	public AdLibRolFileBuilder withTicksPerBeat(int ticksPerBeat) {
		this.ticksPerBeat = ticksPerBeat;
		return this;
	}
	
	public AdLibRolFileBuilder withBeatsPerMeasure(int beatsPerMeasure) {
		this.beatsPerMeasure = beatsPerMeasure;
		return this;
	}
	
	public AdLibRolFileBuilder withMainTempo(float tempo) {
		this.tempo = tempo;
		return this;
	}
	
	public void addTempoMultiplierEvent(int tick, float multiplier) {
		var tempoEvent = new AdLibRolFile.Tempo(tick, multiplier);
		tempoEvents.add(tempoEvent);
	}

	public void addNoteEvent(int channel, int note, int duration) {
		var noteEvent = new AdLibRolFile.Note(note,duration);
		channels[channel].noteEvents.add(noteEvent);
	}
	
	public void addTimbreEvent(int channel, int tick, String instrument) {
		var timbreEvent = new AdLibRolFile.Timbre(
				tick,
				instrument,
				UNKNOWN_INSTUMENT_BYTE);
		channels[channel].timbreEvents.add(timbreEvent);
	}

	public void addVolumeEvent(int channel, int tick, float volume) {
		var volumeEvent = new AdLibRolFile.Volume(tick, volume);
		channels[channel].volumeEvents.add(volumeEvent);
	}

	public void addPitchEvent(int channel, int tick, float pitch) {
		var pitchEvent = new AdLibRolFile.Pitch(tick, pitch);
		channels[channel].pitchEvents.add(pitchEvent);
	}
	
	public AdLibRolFile build() {
		addDefaultEvents();
		
		AdLibRolFile.Header header = buildHeader();
		AdLibRolFile.TempoTrack tempoTrack = buildTempoTrack();
		AdLibRolFile.Channel[] channels = buildChannels();
			
		return new AdLibRolFile(header, tempoTrack, channels);
	}
	
	private void addDefaultEvents() {
		// Add default events as Ad Lib's Visual Composer would have done
		if(tempoEvents.isEmpty() || tempoEvents.get(0).getTick() > 0) {
			tempoEvents.add(0, new AdLibRolFile.Tempo(0, DEFAULT_TEMPO_MULTIPLIER));
		}
		for (int i = 0; i < CHANNELS; i++) {
			final ChannelEventLists events = channels[i];

			if(events.timbreEvents.isEmpty() || events.timbreEvents.get(0).getTick() > 0) {
				String defaultTimbre = getDefaultTimbreName(i);
				events.timbreEvents.add(0, new AdLibRolFile.Timbre(0, defaultTimbre, UNKNOWN_INSTUMENT_BYTE));
			}

			if(events.volumeEvents.isEmpty() || events.volumeEvents.get(0).getTick() > 0) {
				events.volumeEvents.add(0, new AdLibRolFile.Volume(0, DEFAULT_VOLUME_MULTIPLIER));
			}

			if(events.pitchEvents.isEmpty() || events.pitchEvents.get(0).getTick() > 0) {
				events.pitchEvents.add(0, new AdLibRolFile.Pitch(0, DEFAULT_PITCH_MULTIPLIER));
			}
		}
	}
	
	private String getDefaultTimbreName(int channel) {
		if(songMode == MELODIC_SONG_MODE) {
			return DEFAULT_MELODIC_TIMBRE;
		}
		switch(channel) {
			case 0: 
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				return DEFAULT_MELODIC_TIMBRE;
			case 6:
				return "bdrum1";
			case 7:
				return "snare1";
			case 8:
				return "tom1";
			case 9:
				return "cymbal1";
			case 10:
				return "hihat1";
		}
		
		throw new IllegalStateException("Unexpected channel: " + channel);
	}
	
	private AdLibRolFile.Header buildHeader() {
		return new AdLibRolFile.Header(
				SIGNATURE,
				ticksPerBeat,
				beatsPerMeasure,
				SCALE_Y,
				SCALE_X,
				songMode,
				calcCounters());		
	}
	
	private AdLibRolFile.TempoTrack buildTempoTrack() {
		return new AdLibRolFile.TempoTrack(
				TEMPO_TRACK_NAME,
				tempo,
				tempoEvents);
	}

	private AdLibRolFile.Channel[] buildChannels() {
		AdLibRolFile.Channel[] channels = new AdLibRolFile.Channel[CHANNELS];
		for(int i = 0; i < CHANNELS; i++) {
			AdLibRolFile.VoiceTrack voiceTrack = new AdLibRolFile.VoiceTrack(
					VOICE_TRACK_PREFIX + channelInNameSuffix(i),
					getSumDurationNotes(i),
					this.channels[i].noteEvents);
			
			AdLibRolFile.TimbreTrack timbreTrack = new AdLibRolFile.TimbreTrack(
					TIMBRE_TRACK_PREFIX + channelInNameSuffix(i),
					this.channels[i].timbreEvents);
			
			AdLibRolFile.VolumeTrack volumeTrack = new AdLibRolFile.VolumeTrack(
					VOLUME_TRACK_PREFIX + channelInNameSuffix(i),
					this.channels[i].volumeEvents);

			AdLibRolFile.PitchTrack pitchTrack = new AdLibRolFile.PitchTrack(
					PITCH_TRACK_PREFIX + channelInNameSuffix(i),
					this.channels[i].pitchEvents);

			channels[i] = new AdLibRolFile.Channel(voiceTrack, timbreTrack, volumeTrack, pitchTrack);
		}
		return channels;
	}
	
	private String channelInNameSuffix(int i) {
		return i < 10 ? String.valueOf(i) : " " + String.valueOf(i);
	}
	
	private int[] calcCounters() {
		int[] counters = new int[45];

		int nextCounter = 0;

		for(int i = 0; i < CHANNELS; i++)
			counters[nextCounter++] = getSumDurationNotes(i);

		for(int i = 0; i < CHANNELS; i++)
			counters[nextCounter++] = channels[i].timbreEvents.size();

		for(int i = 0; i < CHANNELS; i++)
			counters[nextCounter++] = channels[i].volumeEvents.size();
		
		for(int i = 0; i < CHANNELS; i++)
			counters[nextCounter++] = channels[i].pitchEvents.size();
		
		counters[nextCounter++] = tempoEvents.size();
		
		return counters;
	}
	
	private int getSumDurationNotes(int channel) {
		return channels[channel].noteEvents.stream()
		.map(AdLibRolFile.Note::getDuration)
		.reduce(0, Integer::sum);		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(channels);
		result = prime * result + Objects.hash(beatsPerMeasure, songMode, tempo, tempoEvents, ticksPerBeat);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdLibRolFileBuilder other = (AdLibRolFileBuilder) obj;
		return beatsPerMeasure == other.beatsPerMeasure && Arrays.equals(channels, other.channels)
				&& songMode == other.songMode && Float.floatToIntBits(tempo) == Float.floatToIntBits(other.tempo)
				&& Objects.equals(tempoEvents, other.tempoEvents) && ticksPerBeat == other.ticksPerBeat;
	}

	@Override
	public String toString() {
		return "AdLibRolFileBuilder [songMode=" + songMode + ", tempoEvents=" + tempoEvents + ", channels="
				+ Arrays.toString(channels) + ", ticksPerBeat=" + ticksPerBeat + ", beatsPerMeasure=" + beatsPerMeasure
				+ ", tempo=" + tempo + "]";
	}

	// Stupid container class. No getters/setters as this class is not
	// exposed outside this class.
	private static class ChannelEventLists {
		public List<AdLibRolFile.Note> noteEvents;
		public List<AdLibRolFile.Timbre> timbreEvents;
		public List<AdLibRolFile.Volume> volumeEvents;
		public List<AdLibRolFile.Pitch> pitchEvents;
	}
}

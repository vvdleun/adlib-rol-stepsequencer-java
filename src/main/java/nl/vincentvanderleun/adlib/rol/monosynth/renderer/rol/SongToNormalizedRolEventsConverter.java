package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import nl.vincentvanderleun.adlib.rol.monosynth.song.Song;
import nl.vincentvanderleun.adlib.rol.monosynth.song.Voice;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event.Note;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event.NoteValue;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event.OctaveChange;
import nl.vincentvanderleun.adlib.rol.monosynth.song.pattern.event.PatchChange;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.event.PlayPattern;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.monosynth.renderer.RenderException;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.event.Channel;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.event.Tracks;
import nl.vincentvanderleun.adlib.rol.monosynth.song.Patch;

public class SongToNormalizedRolEventsConverter {
	private static final Map<NoteValue, Integer> NOTE_NUMBERS;
	private static final int DEFAULT_OCTAVE = 4;
	
	private final Song song;
	private final Map<String, Patch> patches;
	private final Map<String, Pattern> patterns;
	
	private int octave;
	private int tick;
	private Patch patch;

	static {
		NOTE_NUMBERS = new HashMap<>(12);
		NOTE_NUMBERS.put(NoteValue.C, 12);
		NOTE_NUMBERS.put(NoteValue.C_SHARP, 13);
		NOTE_NUMBERS.put(NoteValue.D, 14);
		NOTE_NUMBERS.put(NoteValue.D_SHARP, 15);
		NOTE_NUMBERS.put(NoteValue.E, 16);
		NOTE_NUMBERS.put(NoteValue.F, 17);
		NOTE_NUMBERS.put(NoteValue.F_SHARP, 18);
		NOTE_NUMBERS.put(NoteValue.G, 19);
		NOTE_NUMBERS.put(NoteValue.G_SHARP, 20);
		NOTE_NUMBERS.put(NoteValue.A, 21);
		NOTE_NUMBERS.put(NoteValue.A_SHARP, 22);
		NOTE_NUMBERS.put(NoteValue.B, 23);
	}

	public SongToNormalizedRolEventsConverter(Song song) {
		this.tick = 0;
		this.octave = DEFAULT_OCTAVE;
		this.song = song;
		this.patch = song.getPatches().get(0);

		this.patterns = song.getPatterns().stream()
				.collect(Collectors.toMap(
						Pattern::getName, (pattern) -> pattern));

		this.patches = song.getPatches().stream()
				.collect(Collectors.toMap(
						Patch::getName, (patch) -> patch));
	}

	public Tracks convertToNormalizedRolEvents() throws RenderException {
		final Tracks tracks = new Tracks();

		convertSequencerEvents(tracks);

		return tracks;
	}
	
	private void convertSequencerEvents(Tracks tracks) throws RenderException {
		for(nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Event event : song.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					convertPattern(pattern, tracks);
					break;
				default:
					throw new RenderException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private void convertPattern(Pattern pattern, Tracks tracks) throws RenderException {
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case OCTAVE:
					octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					convertPatch((PatchChange)event, tracks);
					break; 
				case NOTE:
					convertNote((Note)event, tracks);
					break;
				case REST:
					// Silence notes are added during rendering. Just skip the ticks.
					++tick;
					break;
				case HOLD:
					System.out.println("- SKipped for now");
					break;
				default:
					throw new RenderException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private void convertPatch(PatchChange patchEvent, Tracks tracks) throws RenderException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);

		this.patch = patches.get(patchName);

		patch.getVoices().forEach(voice -> {
			int voiceChannel = voice.getChannel();
			Channel channel = tracks.getChannels().get(voiceChannel);
			channel.addInstrumentEvent(tick, voice.getInstrument());
			channel.addPitchEvent(tick, voice.getPitch());
			channel.addVolumeEvent(tick, voice.getVolume());
		});
	}

	private void convertNote(Note noteEvent, Tracks tracks) throws RenderException {
		for(Voice voice : patch.getVoices()) {
			final int channel = voice.getChannel();
			final int duration = noteEvent.getDuration();
			final int note = NOTE_NUMBERS.get(noteEvent.getNote()) + (12 * (octave + noteEvent.getOctaveOffset()))
					+ voice.getTranspose();
 
			tracks.getChannels().get(channel).addNoteEvent(tick, new NoteEvent(note, duration));
		}
		++tick;
	}
}

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
import nl.vincentvanderleun.adlib.rol.monosynth.song.Patch;

public class RolEventConverter {
	private static final Map<NoteValue, Integer> NOTE_NUMBERS;
	private static final int DEFAULT_OCTAVE = 4;
	
	private final Song song;
	private final Map<String, Patch> patches;
	private final Map<String, Pattern> patterns;
	private final Tracks rolEvents;
	
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

	public RolEventConverter(Song song) {
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

		this.rolEvents = new Tracks();

		initializeRolEvents();
	}

	private void initializeRolEvents() {
		rolEvents.getTempoEvents().put(0, 1.0f);
		rolEvents.getChannels().forEach(channel -> {
			channel.addInstrumentEvent(0, "PIANO1");
			channel.addPitchEvent(0, 1.0f);
			channel.addVolumeEvent(0, 0.75f);
		});
	}

	public void convertEvents() throws RenderException {
		for(nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Event event : song.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					convertPattern(pattern);
					break;
				default:
					throw new RenderException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private void convertPattern(Pattern pattern) throws RenderException {
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case OCTAVE:
					octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					convertPatch((PatchChange)event);
					break; 
				case NOTE:
					convertNote((Note)event);
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

	private void convertPatch(PatchChange patchEvent) throws RenderException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);

		this.patch = patches.get(patchName);

		patch.getVoices().forEach(voice -> {
			int voiceChannel = voice.getChannel();
			Channel channel = rolEvents.getChannels().get(voiceChannel);
			channel.addInstrumentEvent(tick, voice.getInstrument());
		});
	}

	private void convertNote(Note noteEvent) throws RenderException {
		for(Voice voice : patch.getVoices()) {
			final int channel = voice.getChannel();
			final int duration = noteEvent.getDuration();
			final int note = NOTE_NUMBERS.get(noteEvent.getNote()) + (12 * (octave + noteEvent.getOctaveOffset()))
					+ voice.getTranspose();
 
			rolEvents.getChannels().get(channel).addNoteEvent(tick, new NoteEvent(note, duration));
		}
		++tick;
	}
}

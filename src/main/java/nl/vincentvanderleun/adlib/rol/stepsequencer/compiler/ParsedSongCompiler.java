package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.InstrumentEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.NoteValue;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Note;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.OctaveChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.PatchChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.PlayPattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.RenderException;

public class ParsedSongCompiler {
	private static final Map<NoteValue, Integer> NOTE_NUMBERS;
	private static final int DEFAULT_OCTAVE = 4;
	
	private final ParsedSong parsedSong;
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

	public ParsedSongCompiler(ParsedSong parsedSong) {
		this.tick = 0;
		this.octave = DEFAULT_OCTAVE;
		this.parsedSong = parsedSong;
		this.patch = parsedSong.getPatches().get(0);

		this.patterns = parsedSong.getPatterns().stream()
				.collect(Collectors.toMap(
						Pattern::getName, (pattern) -> pattern));

		this.patches = parsedSong.getPatches().stream()
				.collect(Collectors.toMap(
						Patch::getName, (patch) -> patch));
	}

	public void compile() throws RenderException {
		CompiledSong compiledSong = initializeCompiledSong(parsedSong);
		
		for(nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event event : parsedSong.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					convertPattern(pattern, compiledSong);
					break;
				default:
					throw new RenderException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private CompiledSong initializeCompiledSong(ParsedSong song) {
		return new CompiledSong(
				song.getHeader().getTarget(),
				song.getHeader().getTempo(),
				song.getHeader().getTicksPerBeat(),
				song.getHeader().getBeatsPerMeasure(),
				song.getHeader().getMode());
	}
	
	private void convertPattern(Pattern pattern, CompiledSong song) throws RenderException {
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case OCTAVE:
					octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					convertPatch((PatchChange)event, song);
					break; 
				case NOTE:
					convertNote((Note)event, song);
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

	private void convertPatch(PatchChange patchEvent, CompiledSong song) throws RenderException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);

		this.patch = patches.get(patchName);

		patch.getVoices().forEach(voice -> {
			int voiceChannel = voice.getChannel();
			Channel channel = song.getChannels().get(voiceChannel);
			channel.addInstrumentEvent(tick, new InstrumentEvent(voice.getInstrument()));
			channel.addPitchEvent(tick, new PitchMultiplierEvent(voice.getPitch()));
			channel.addVolumeEvent(tick, new VolumeMultiplierEvent(voice.getVolume()));
		});
	}

	private void convertNote(Note noteEvent, CompiledSong song) throws RenderException {
		for(Voice voice : patch.getVoices()) {
			final int channel = voice.getChannel();
			final int duration = noteEvent.getDuration();
			final int note = NOTE_NUMBERS.get(noteEvent.getNote()) + (12 * (octave + noteEvent.getOctaveOffset()))
					+ voice.getTranspose();
 
			song.getChannels().get(channel).addNoteEvent(tick, new NoteEvent(note, duration));
		}
		++tick;
	}
}

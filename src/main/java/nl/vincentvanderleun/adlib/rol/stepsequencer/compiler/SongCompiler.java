package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.InstrumentEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.OctaveChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.PatchChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.PlayPattern;

public class SongCompiler {
	private static final int DEFAULT_OCTAVE = 4;
	
	private final ParsedSong parsedSong;
	private final Map<String, Patch> patches;
	private final Map<String, Pattern> patterns;
	
	private int octave;
	private int tick;
	private Patch patch;

	public static CompiledSong compile(ParsedSong parsedSong) throws CompileException {
		int eventsPreProcessed = PreProcessor.process(parsedSong);
		System.out.println(eventsPreProcessed + " event(s) pre-processed...");
		
		var compiler = new SongCompiler(parsedSong);

		CompiledSong compiledSong = compiler.compile();
	
		return compiledSong;
	}
	
	private SongCompiler(ParsedSong parsedSong) {
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

	public CompiledSong compile() throws CompileException {
		CompiledSong compiledSong = initializeCompiledSong(parsedSong);
		
		for(nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event event : parsedSong.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					convertPattern(pattern, compiledSong);
					break;
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
		
		return compiledSong;
	}

	private CompiledSong initializeCompiledSong(ParsedSong song) {
		return new CompiledSong(
				song.getHeader().getTarget(),
				song.getHeader().getTempo(),
				song.getHeader().getTicksPerBeat(),
				song.getHeader().getBeatsPerMeasure(),
				song.getHeader().getMode());
	}
	
	private void convertPattern(Pattern pattern, CompiledSong song) throws CompileException {
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case OCTAVE:
					octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					convertPatch((PatchChange)event, song);
					break; 
				case NOTE:
					convertNote((nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent)event, song);
					break;
				case REST:
					// Silence notes are handled during rendering. Just skip the ticks.
					++tick;
					break;
				case FUNCTION:
				case HOLD:
					throw new CompileException("Internal error: event " + event.getEventType() + " was supposed to be handled by pre-processor");
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private void convertPatch(PatchChange patchEvent, CompiledSong song) throws CompileException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);

		this.patch = patches.get(patchName);

		for(int voiceIndex = 0; voiceIndex < patch.getVoices().size(); voiceIndex++) {
			Voice voice = patch.getVoices().get(voiceIndex);

			Channel channel = song.getChannels().get(voiceIndex);
			channel.addInstrumentEvent(tick, new InstrumentEvent(voice.getInstrument()));
			channel.addPitchEvent(tick, new PitchMultiplierEvent(voice.getPitch()));
			channel.addVolumeEvent(tick, new VolumeMultiplierEvent(voice.getVolume()));
		};
	}

	private void convertNote(nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent parsedNote, CompiledSong song) throws CompileException {
		int channel = 0;

		for(Voice voice : patch.getVoices()) {
			final NoteEvent noteEvent = new NoteEvent(
					parsedNote.getNote(),
					parsedNote.getDuration(),
					this.octave + parsedNote.getOctaveOffset(),
					voice.getTranspose());
			
			final int noteTick = tick + voice.getOffset();
			
			if(noteTick >= 0) {
				song.getChannels().get(channel++).addNoteEvent(noteTick, noteEvent);
			} else {
				System.out.println("Warning: Discarded note on voice \"" + voice.getName() + "\" of patch \"" + patch.getName() + "\", because calculated tick was < 0");
			}
		}
		tick += parsedNote.getDuration();
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
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

public class PatternCompiler {
	private static final int DEFAULT_OCTAVE = 4;

	private final ParsedSong parsedSong;
	private final CompiledSong compiledSong;
	private final Map<String, Patch> patches;
	
	public PatternCompiler(ParsedSong parsedSong, CompiledSong compiledSong) {
		this.parsedSong = parsedSong;
		this.compiledSong = compiledSong;
		
		this.patches = parsedSong.getPatches().stream()
				.collect(Collectors.toMap(
						Patch::getName, (patch) -> patch));
	}

	public void compile(Pattern pattern, CompilerContext context) throws CompileException {
		context.octave = DEFAULT_OCTAVE;
		context.patch = parsedSong.getPatches().get(0);
		
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case OCTAVE:
					context.octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					convertPatch((PatchChange)event, context);
					break; 
				case NOTE:
					convertNote((nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent)event, context);
					break;
				case REST:
					// Silence notes are handled during rendering. Just skip the ticks.
					++context.tick;
					break;
				case FUNCTION:
				case HOLD:
					throw new CompileException("Internal error: event " + event.getEventType() + " was supposed to be handled by pre-processor");
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

	private void convertPatch(PatchChange patchEvent, CompilerContext context) throws CompileException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);
		
		for(int voiceIndex = 0; voiceIndex < patch.getVoices().size(); voiceIndex++) {
			Voice voice = patch.getVoices().get(voiceIndex);

			int tick = context.tick + voice.getOffset();
			if(tick < 0) {
				System.out.println("Warning: voice offset of patch makes instrument change on tick < 0. Setting on tick 0 instead.");
				tick = 0;
			}
			
			Channel channel = compiledSong.getChannels().get(voiceIndex);
			channel.addInstrumentEvent(tick, new InstrumentEvent(voice.getInstrument()));
			channel.addPitchEvent(tick, new PitchMultiplierEvent(voice.getPitch()));
			channel.addVolumeEvent(tick, new VolumeMultiplierEvent(voice.getVolume()));
		};
	}

	private void convertNote(nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent parsedNote, CompilerContext context) throws CompileException {
		int channel = 0;

		for(Voice voice : context.patch.getVoices()) {
			final NoteEvent noteEvent = new NoteEvent(
					parsedNote.getNote(),
					parsedNote.getDuration(),
					context.octave + parsedNote.getOctaveOffset(),
					voice.getTranspose());
			
			final int noteTick = context.tick + voice.getOffset();
			
			if(noteTick >= 0) {
				compiledSong.getChannels().get(channel++).addNoteEvent(noteTick, noteEvent);
			} else {
				System.out.println("Warning: Discarded note on voice \"" + voice.getName() + "\" of patch \"" + context.patch.getName() + "\", because calculated tick was < 0");
			}
		}
		
		context.tick += parsedNote.getDuration();
	}

}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.pattern.OctaveChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.pattern.PatchChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.FloatDiffUtils;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pitch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;

public class PatternCompiler {
	private static final int DEFAULT_OCTAVE = 4;

	private final Track track;
	private final ParsedSong parsedSong;
	private final Map<String, Patch> patches;
	
	public PatternCompiler(Track track, ParsedSong parsedSong) {
		this.track = track;
		this.parsedSong = parsedSong;
		
		this.patches = parsedSong.getPatches().stream()
				.collect(Collectors.toMap(
						Patch::getName, (patch) -> patch));
	}

	public void compile(Pattern pattern, CompilerContext context) throws CompileException {
		context.octave = DEFAULT_OCTAVE;

		// First patch is always the default at the start of a pattern for now
		track.registerPatchChange(context.tick, parsedSong.getPatches().get(0));
		
		for(Event event : pattern.getEvents()) {
			switch(event.getEventType()) {
				case HOLD:
					throw new CompileException("Internal error: event " + event.getEventType() + " was supposed to be handled by pre-processor");
				case NOTE:
					compileNote(pattern, (nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent)event, context);
					break;
				case REST:
					compileRest(((Rest)event), context);
					break;
				case PITCH:
					compilePitch(pattern, (Pitch)event, context);
					break;
				case FUNCTION:
					compileFunctionCall(pattern, (FunctionCall)event, context);
					break;
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" event is not implemented yet");
			}
		}
	}

	private void compileRest(Rest rest, CompilerContext context) {
		// Silence notes are handled during rendering. Just skip the ticks.
		context.tick += rest.getDuration();
	}
	
	private void compilePitch(Pattern pattern, Pitch pitchEvent, CompilerContext context) throws CompileException {
		final Patch patch = getCurrentPatch(context.tick);

		final List<Channel> patchChannels = track.claimChannels(patch.getVoices().size());
	
		int channel = 0;
		for(Voice voice : patch.getVoices()) {
			// Try to keep ratio of voice intact
			float value = FloatDiffUtils.changePitchAndKeepRatio(voice.getPitch(), pitchEvent.getPitch());
			
			final float previousValue = patchChannels.get(channel).getEventsAtOrBeforeTick(context.tick).getPitch();

			final var compiledPitchEvent = new PitchMultiplierEvent(value);
			patchChannels.get(channel).addPitchEvent(context.tick, compiledPitchEvent);

			if(pitchEvent.getDuration() > 1) {
				var compiledPreviousValue = new PitchMultiplierEvent(previousValue);
				patchChannels.get(channel).addPitchEvent(context.tick + pitchEvent.getDuration() - 1, compiledPreviousValue);
			}

			channel++;
		}
	}
	
	private void compileNote(Pattern pattern, nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent parsedNote, CompilerContext context) throws CompileException {
		final Patch patch = getCurrentPatch(context.tick);
		final List<Channel> patchChannels = track.claimChannels(patch.getVoices().size());

		int channel = 0;

		for(Voice voice : patch.getVoices()) {
			final NoteEvent noteEvent = new NoteEvent(
					parsedNote.getNote(),
					parsedNote.getDuration(),
					context.octave + parsedNote.getOctaveOffset(),
					voice.getTranspose());

			final int noteTick = context.tick + voice.getOffset();
			
			// Claim start/end ticks for the track
			track.claimTickOnChannels(noteTick);
			track.claimTickOnChannels(noteTick + noteEvent.getDuration() - 1);

			if(noteTick >= 0) {
				patchChannels.get(channel++).addNoteEvent(noteTick, noteEvent);
			} else {
				System.out.println("Warning: Discarded note on voice \"" + voice.getName() + "\" of patch \"" + patch.getName() + "\", because calculated tick was < 0");
			}
		}

		context.tick += parsedNote.getDuration();
	}

	private void compileFunctionCall(Pattern pattern, FunctionCall functionCall, CompilerContext context) throws CompileException {
		switch(functionCall.getFunctionName()) {
			case "octave":
				OctaveChange octaveChange = new OctaveChange();
				octaveChange.execute(track, context, functionCall.getArguments());
				break;
			case "patch":
				PatchChange patchChange = new PatchChange(patches);
				patchChange.execute(track, context, functionCall.getArguments());
				break;
			default:
				throw new CompileException("Unknown function call \"" + functionCall.getFunctionName() 
						+ "\" in pattern \"" + pattern.getName() + "\"");
		}
	}

	private Patch getCurrentPatch(int tick) {
		return track.getActivePatchAtTick(tick);
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
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
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pitch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;
import nl.vincentvanderleun.adlib.rol.stepsequencer.util.FloatDiffUtils;

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
					convertNote(pattern, (nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent)event, context);
					break;
				case OCTAVE:
					context.octave = ((OctaveChange)event).getOctave();
					break;
				case PATCH:
					PatchChange patchChangeEvent = (PatchChange)event;
					convertPatch(patchChangeEvent, context);
					track.registerPatchChange(context.tick, patches.get(patchChangeEvent.getPatchName()));
					break; 
				case PITCH:
					convertPitch(pattern, (Pitch)event, context);
					break;
				case REST:
					// Silence notes are handled during rendering. Just skip the ticks.
					context.tick += ((Rest)event).getDuration();
					break;
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" event is not implemented yet");
			}
		}
	}

	private void convertPatch(PatchChange patchEvent, CompilerContext context) throws CompileException {
		final String patchName = patchEvent.getPatchName();
		final Patch patch = patches.get(patchName);

		final List<Channel> patchChannels = track.claimChannels(patch.getVoices().size());
	
		for(int voiceIndex = 0; voiceIndex < patch.getVoices().size(); voiceIndex++) {
			final Voice voice = patch.getVoices().get(voiceIndex);
		
			int tick = context.tick + voice.getOffset();
			if(tick < 0) {
				System.out.println("Warning: Moved patch change of voice \"" + voice.getName() 
						+ "\" of patch \"" + patchEvent.getPatchName() 
						+ "\" to tick 0, because of its offset the calculated tick was < 0.");
				tick = 0;
			}

			Channel channel = patchChannels.get(voiceIndex);
			channel.addInstrumentEvent(tick, new InstrumentEvent(voice.getInstrument()));
			channel.addPitchEvent(tick, new PitchMultiplierEvent(voice.getPitch()));
			channel.addVolumeEvent(tick, new VolumeMultiplierEvent(voice.getVolume()));
		};
	}

	private void convertPitch(Pattern pattern, Pitch pitchEvent, CompilerContext context) throws CompileException {
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
	
	private void convertNote(Pattern pattern, nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent parsedNote, CompilerContext context) throws CompileException {
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
	
	private Patch getCurrentPatch(int tick) {
		return track.getActivePatchAtTick(tick);
	}
}

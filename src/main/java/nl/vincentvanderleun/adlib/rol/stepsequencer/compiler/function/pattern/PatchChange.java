package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.pattern;

import java.util.List;
import java.util.Map;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.InstrumentEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;

public class PatchChange extends CompilablePatternFunction {
	private static final String FUNCTION_NAME = "patch";

	private final Map<String, Patch> patches;
	
	public PatchChange(Map<String, Patch> patches) {
		super(FUNCTION_NAME);
		
		this.patches = patches;
	}

	@Override
	public void execute(Track track, CompilerContext context, List<Object> arguments) throws CompileException {
		final String patchName = (String)arguments.get(0);
		final Patch patch = getPatch(patchName);
		
		final List<Channel> patchChannels = track.claimChannels(patch.getVoices().size());
	
		for(int voiceIndex = 0; voiceIndex < patch.getVoices().size(); voiceIndex++) {
			final Voice voice = patch.getVoices().get(voiceIndex);
		
			int tick = context.tick + voice.getOffset();
			if(tick < 0) {
				System.out.println("Warning: Moved patch change of voice \"" + voice.getName() 
						+ "\" of patch \"" + patchName 
						+ "\" to tick 0, because of its offset the calculated tick was < 0.");
				tick = 0;
			}

			Channel channel = patchChannels.get(voiceIndex);
			setVoiceEventsOnTick(tick, channel, voice);
		};
		
		track.registerPatchChange(context.tick, patch);
	}

	private void setVoiceEventsOnTick(int tick, Channel channel, Voice voice) {
		channel.addInstrumentEvent(tick, new InstrumentEvent(voice.getInstrument()));
		channel.addPitchEvent(tick, new PitchMultiplierEvent(voice.getPitch()));
		channel.addVolumeEvent(tick, new VolumeMultiplierEvent(voice.getVolume()));
	}
	
	private Patch getPatch(String patchName) throws CompileException {
		final Patch patch = patches.get(patchName);
		if(patch == null) {
			throw new CompileException("Cannot switch to unknown patch \"" + patchName + "\"");
		}
		return patch;
	}
}

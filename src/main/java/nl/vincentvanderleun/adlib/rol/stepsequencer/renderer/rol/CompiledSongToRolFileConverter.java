package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.ChannelEvents;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.NoteValue;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFileBuilder;

public class CompiledSongToRolFileConverter {
	private static final int SILENCE = 0;
	private static final Map<NoteValue, Integer> NOTE_NUMBERS;

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
	
	public AdLibRolFile convertToAdLibRolFile(CompiledSong song) throws IOException {
		AdLibRolFileBuilder builder = buildRolBuilder(song.getSongMode())
				.withTicksPerBeat(song.getTicksPerBeat())
				.withBeatsPerMeasure(song.getBeatsPerMeasure())
				.withMainTempo(song.getTempo());
		
		for(Map.Entry<Integer, Float> tempoEvent : song.getTempoEvents().entrySet()) {
			builder.addTempoMultiplierEvent(tempoEvent.getKey(), tempoEvent.getValue());
		}

		for(Channel channel : song.getChannels()) {
			int lastNoteTick = 0;
			for(ChannelEvents events : channel.getAllEvents()) {
				if(events.getNote() != null) {
					// System.out.println(events.getTick());
					if(events.getTick() > lastNoteTick) {
						// Add rest
						int restDuration = events.getTick() - lastNoteTick;
						builder.addNoteEvent(events.getChannel(), SILENCE, restDuration);
					}
					
					final int noteNumber = convertCompiledNoteEvent(events.getNote());

					builder.addNoteEvent(events.getChannel(), noteNumber, events.getNote().getDuration());
					lastNoteTick = events.getTick() + events.getNote().getDuration();
				}
				
				if(events.getInstrument() != null) {
					builder.addTimbreEvent(events.getChannel(), events.getTick(), events.getInstrument());
				}
				
				if(events.getVolume() != null) {
					builder.addVolumeEvent(events.getChannel(), events.getTick(), events.getVolume());
				}

				if(events.getPitch() != null) {
					builder.addPitchEvent(events.getChannel(), events.getTick(), events.getPitch());
				}
			}
		}
		
		return builder.build();
	}


	private int convertCompiledNoteEvent(NoteEvent noteEvent) {
		return NOTE_NUMBERS.get(noteEvent.getNote())
				+ (12 * noteEvent.getOctave())
				+ noteEvent.getTranspose();
	}
	
	private AdLibRolFileBuilder buildRolBuilder(SongMode songMode) {
		switch(songMode) {
			case MELODIC:
				return AdLibRolFileBuilder.createMelodicRolFile();
			case PERCUSSIVE:
				return AdLibRolFileBuilder.createPercussiveRolFile();
		}
		
		throw new IllegalStateException("Unsupported song mode: " + songMode);
	}
	
}

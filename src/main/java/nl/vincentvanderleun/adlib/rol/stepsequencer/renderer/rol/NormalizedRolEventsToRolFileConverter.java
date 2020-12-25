package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol;

import java.io.IOException;
import java.util.Map;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.Song;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.event.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.event.ChannelEvents;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.event.Tracks;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer.AdLibRolFileBuilder;

public class NormalizedRolEventsToRolFileConverter {
	private static final int SILENCE = 0;
	
	public AdLibRolFile convertToAdLibRolFile(Song song, Tracks normalizedEvents) throws IOException {
		AdLibRolFileBuilder builder = buildRolBuilder(song.getHeader().getMode())
				.withTicksPerBeat(song.getHeader().getTicksPerBeat())
				.withBeatsPerMeasure(song.getHeader().getBeatsPerMeasure())
				.withMainTempo(song.getHeader().getTempo());
		
		for(Map.Entry<Integer, Float> tempoEvent : normalizedEvents.getTempoEvents().entrySet()) {
			builder.addTempoMultiplierEvent(tempoEvent.getKey(), tempoEvent.getValue());
		}

		for(Channel channel : normalizedEvents.getChannels()) {
			int lastNoteTick = 0;
			for(ChannelEvents events : channel.getAllEvents()) {
				if(events.getNote() != null) {
					// System.out.println(events.getTick());
					if(events.getTick() > lastNoteTick) {
						// Add rest
						int restDuration = events.getTick() - lastNoteTick;
						builder.addNoteEvent(events.getChannel(), SILENCE, restDuration);
					}
					builder.addNoteEvent(events.getChannel(), events.getNote().getNote(), events.getNote().getDuration());
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

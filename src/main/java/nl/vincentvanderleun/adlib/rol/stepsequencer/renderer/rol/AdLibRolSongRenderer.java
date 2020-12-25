package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.Song;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.event.Tracks;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer.AdLibRolFileWriter;

public class AdLibRolSongRenderer {
	
	public void renderRolFile(Song song, OutputStream outputStream) throws IOException {
		SongToNormalizedRolEventsConverter songToRolConverter = new SongToNormalizedRolEventsConverter(song);
		
		Tracks normalizedRolEvents = songToRolConverter.convertToNormalizedRolEvents();
	
		NormalizedRolEventsToRolFileConverter normalizedEventsConverter = new NormalizedRolEventsToRolFileConverter();
		
		AdLibRolFile rol = normalizedEventsConverter.convertToAdLibRolFile(song, normalizedRolEvents);
		
		AdLibRolFileWriter writer = new AdLibRolFileWriter();
		
		writer.writeRolFile(rol, outputStream);
	}
}

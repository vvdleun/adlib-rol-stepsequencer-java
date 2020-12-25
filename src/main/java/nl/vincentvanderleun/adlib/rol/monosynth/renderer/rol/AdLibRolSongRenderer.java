package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.event.Tracks;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.writer.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.writer.AdLibRolFileWriter;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.Song;

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

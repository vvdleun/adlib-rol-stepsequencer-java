package nl.vincentvanderleun.adlib.rol.monosynth.renderer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.song.Song;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol.AdLibRolSongRenderer;

public class OutputFileRenderer {
	
	public void renderToAdLibRolFile(Song song, String path) throws IOException {
		try(var outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
			AdLibRolSongRenderer adLibRolSongRenderer = new AdLibRolSongRenderer();
			adLibRolSongRenderer.renderRolFile(song, outputStream);
		}
	}
	
}

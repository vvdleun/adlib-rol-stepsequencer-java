package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.AdLibRolSongRenderer;

public class OutputFileRenderer {
	
	public void renderToAdLibRolFile(CompiledSong song, String path) throws IOException {
		try(var outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
			AdLibRolSongRenderer adLibRolSongRenderer = new AdLibRolSongRenderer();
			adLibRolSongRenderer.renderRolFile(song, outputStream);
		}
	}
	
}

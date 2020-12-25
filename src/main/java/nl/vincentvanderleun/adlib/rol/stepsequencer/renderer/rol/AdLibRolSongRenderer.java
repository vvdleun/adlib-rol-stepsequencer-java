package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.ParsedSongCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFileWriter;

public class AdLibRolSongRenderer {
	
	public void renderRolFile(CompiledSong song, OutputStream outputStream) throws IOException {
		CompiledSongToRolFileConverter compiledSongToRolFileConverter = new CompiledSongToRolFileConverter();
		
		AdLibRolFile rol = compiledSongToRolFileConverter.convertToAdLibRolFile(song);
		
		AdLibRolFileWriter writer = new AdLibRolFileWriter();
		
		writer.writeRolFile(rol, outputStream);
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer;

import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.SongCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.SongParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.OutputFileRenderer;

public class App {
    public static void main(String[] args) throws IOException {
		ParsedSong parsedSong = SongParser.parse("C:\\Users\\Vincent\\eclipse-workspace\\adlib-rol-monosynth-java\\sample.mss.txt");

		CompiledSong compiledSong = compileSong(parsedSong);

		renderAdLibRolFile(compiledSong, "c:\\dos\\temp\\test.rol");
    }

    private static CompiledSong compileSong(ParsedSong parsedSong) throws IOException {
		SongCompiler compiler = new SongCompiler(parsedSong);
		
		return compiler.compile();
    }
    
    private static void renderAdLibRolFile(CompiledSong compiledSong, String path) throws IOException {
		OutputFileRenderer outputFileRenderer = new OutputFileRenderer();
		outputFileRenderer.renderToAdLibRolFile(compiledSong, path);
    }
}

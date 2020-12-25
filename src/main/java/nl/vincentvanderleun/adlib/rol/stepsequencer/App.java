package nl.vincentvanderleun.adlib.rol.stepsequencer;

import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.StepSequencerInputFileParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.OutputFileRenderer;

public class App {
    public static void main(String[] args) throws IOException {
		ParsedSong parsedSong = StepSequencerInputFileParser.parse("C:\\Users\\Vincent\\eclipse-workspace\\adlib-rol-monosynth-java\\sample.mss.txt");

		CompiledSong compiledSong = null;
		
		OutputFileRenderer outputFileRenderer = new OutputFileRenderer();
		outputFileRenderer.renderToAdLibRolFile(compiledSong, "c:\\dos\\temp\\test.rol");
    }
}

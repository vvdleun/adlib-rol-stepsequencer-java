package nl.vincentvanderleun.adlib.rol.monosynth;

import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.MonoSynthInputFileParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.song.Song;
import nl.vincentvanderleun.adlib.rol.monosynth.renderer.OutputFileRenderer;

public class App {
    public static void main(String[] args) throws IOException {
		Song song = MonoSynthInputFileParser.parse("C:\\Users\\Vincent\\eclipse-workspace\\adlib-rol-monosynth-java\\sample.mss.txt");
		OutputFileRenderer outputFileRenderer = new OutputFileRenderer();
		outputFileRenderer.renderToAdLibRolFile(song, "c:\\dos\\temp\\test.rol");
    }
}

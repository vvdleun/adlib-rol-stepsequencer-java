package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.SequencerCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;

public class SongCompiler {
	private final ParsedSong parsedSong;
	
	public static CompiledSong compile(ParsedSong parsedSong) throws CompileException {
		int eventsPreProcessed = PreProcessor.process(parsedSong);
		System.out.println(eventsPreProcessed + " event(s) pre-processed...");
		
		var compiler = new SongCompiler(parsedSong);

		CompiledSong compiledSong = compiler.compile();
	
		return compiledSong;
	}
	
	private SongCompiler(ParsedSong parsedSong) {
		this.parsedSong = parsedSong;

	}

	public CompiledSong compile() throws CompileException {
		CompiledSong compiledSong = initializeCompiledSong(parsedSong);

		CompilerContext context = new CompilerContext();
		context.tick = 0;

		SequencerCompiler.compile(parsedSong, compiledSong, context);

		return compiledSong;
	}

	private CompiledSong initializeCompiledSong(ParsedSong song) {
		return new CompiledSong(
				song.getHeader().getTarget(),
				song.getHeader().getTempo(),
				song.getHeader().getTicksPerBeat(),
				song.getHeader().getBeatsPerMeasure(),
				song.getHeader().getMode());
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.PreProcessor;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.SequencerCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;

public class SongCompiler {
	private final ParsedSong parsedSong;
	
	public static CompiledSong compile(ParsedSong parsedSong) throws CompileException {
		PreProcessor.process(parsedSong);
		
		var compiler = new SongCompiler(parsedSong);

		CompiledSong compiledSong = compiler.compile();
	
		return compiledSong;
	}
	
	private SongCompiler(ParsedSong parsedSong) {
		this.parsedSong = parsedSong;

	}

	public CompiledSong compile() throws CompileException {
		CompiledSong compiledSong = initializeCompiledSong(parsedSong);

		ChannelManager channelManager = new ChannelManager(compiledSong.getSongMode());
		CompilerContext context = new CompilerContext();
		
		SequencerCompiler.compile(parsedSong, compiledSong, channelManager, context);

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

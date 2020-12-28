package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.PreProcessor;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block.TrackCompiler;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Channel;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.ChannelEvents;
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

		TrackCompiler.compile(parsedSong, compiledSong, channelManager, context);

		long stats = calcGeneratedEvents(compiledSong);
		System.out.println(stats + " event(s) were generated before conversion to ROL file");

		return compiledSong;
	}

	private long calcGeneratedEvents(CompiledSong song) {
		long countEvents = song.getTempoEvents().size();

		for(Channel channel : song.getChannels()) {
			for(ChannelEvents event : channel.getAllEvents()) {
				if(event.getNote() != null) {
					countEvents++;
				}
				if(event.getInstrument() != null) {
					countEvents++;
				}
				if(event.getVolume() != null) {
					countEvents++;
				}
				if(event.getPitch() != null) {
					countEvents++;
				}
			}
		}
		return countEvents;
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

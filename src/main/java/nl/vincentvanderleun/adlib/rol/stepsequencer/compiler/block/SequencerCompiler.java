package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.PlayPattern;

public class SequencerCompiler {
	private final ParsedSong parsedSong;
	private final CompiledSong compiledSong;
	private final CompilerContext context;
	private final Map<String, Pattern> patterns;
	
	public static void compile(ParsedSong parsedSong, CompiledSong compiledSong, CompilerContext context) throws CompileException {
		SequencerCompiler compiler = new SequencerCompiler(parsedSong, compiledSong, context);
		compiler.compile();
	}
	
	private SequencerCompiler(ParsedSong parsedSong, CompiledSong compiledSong, CompilerContext context) {
		this.parsedSong = parsedSong;
		this.compiledSong = compiledSong;
		this.context = context;

		this.patterns = parsedSong.getPatterns().stream()
				.collect(Collectors.toMap(
						Pattern::getName, (pattern) -> pattern));
	}
	
	public void compile() throws CompileException {
		PatternCompiler patternCompiler = new PatternCompiler(parsedSong, compiledSong);
		
		for(nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event event : parsedSong.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					patternCompiler.compile(pattern, context);
					break;
				default:
					throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
	}

}

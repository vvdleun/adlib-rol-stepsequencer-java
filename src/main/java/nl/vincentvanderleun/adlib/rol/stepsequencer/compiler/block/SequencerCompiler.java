package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Sequence;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.PlayPattern;

public class SequencerCompiler {
	private final ParsedSong parsedSong;
	private final CompiledSong compiledSong;
	private final ChannelManager channelManager;
	private final CompilerContext context;
	private final Map<String, Pattern> patterns;
	
	public static void compile(ParsedSong parsedSong, CompiledSong compiledSong, ChannelManager channelManager, CompilerContext context) throws CompileException {
		SequencerCompiler compiler = new SequencerCompiler(parsedSong, compiledSong, channelManager, context);
		compiler.compile();
	}
	
	private SequencerCompiler(ParsedSong parsedSong, CompiledSong compiledSong, ChannelManager channelManager, CompilerContext context) {
		this.parsedSong = parsedSong;
		this.compiledSong = compiledSong;
		this.channelManager = channelManager;
		this.context = context;

		this.patterns = parsedSong.getPatterns().stream()
				.collect(Collectors.toMap(
						Pattern::getName, (pattern) -> pattern));
	}
	
	public void compile() throws CompileException {
		Sequence sequence = new Sequence(compiledSong, channelManager);
		
		// For now each and every sequence starts on the start of the song
		context.tick = 0;

		PatternCompiler patternCompiler = new PatternCompiler(sequence, parsedSong, compiledSong);
		
		var functionCalls = new ArrayList<ContextAwareFunctionCall>();
		
		for(Event event : parsedSong.getSequencer().getEvents()) {
			switch(event.getEventType() ) {
				case PLAY_PATTERN:
					PlayPattern playPattern = (PlayPattern)event;
					Pattern pattern = patterns.get(playPattern.getPatternName());
					patternCompiler.compile(pattern, context);
					break;
				case FUNCTION_CALL:
					functionCalls.add(new ContextAwareFunctionCall((FunctionCall)event, context.tick));
					break;
				default:
					 throw new CompileException("Internal error: support for \"" + event.getEventType() + "\" is not implemented");
			}
		}
		
		// Now that all events are generated, execute the function calls
		for (ContextAwareFunctionCall call : functionCalls) {
			// Stupid checked exceptions handling in forEach lambdas in Java...
			compileFunctionCall(call.getTick(), call.getFunctionCall());
		}
	}
	
	private void compileFunctionCall(int tick, FunctionCall functionCall) throws CompileException {
		switch(functionCall.getFunctionName()) {
			case "fade-in":
				executeFadeIn(tick, functionCall.getArguments());
				break;
			default:
				throw new CompileException("Unknown function: " + functionCall.getFunctionName());
		}
		
	}

	private void executeFadeIn(int tick, List<String> arguments) {
	}
	
	private static class ContextAwareFunctionCall {
		private final FunctionCall functionCall;
		private final int tick;
		
		public ContextAwareFunctionCall(FunctionCall functionCall, int tick) {
			this.functionCall = functionCall;
			this.tick = tick;
		}

		public FunctionCall getFunctionCall() {
			return functionCall;
		}

		public int getTick() {
			return tick;
		}
	}
}

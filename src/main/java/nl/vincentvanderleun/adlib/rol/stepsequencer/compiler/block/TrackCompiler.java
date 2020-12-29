package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track.FadeInFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.function.track.FadeOutFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.ChannelManager;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.impl.CompilerContext;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.PlayPattern;

public class TrackCompiler {
	private final ParsedSong parsedSong;
	private final CompiledSong compiledSong;
	private final ChannelManager channelManager;
	private final CompilerContext context;
	private final Map<String, Pattern> patterns;
	
	public static void compile(ParsedSong parsedSong, CompiledSong compiledSong, ChannelManager channelManager, CompilerContext context) throws CompileException {
		TrackCompiler compiler = new TrackCompiler(parsedSong, compiledSong, channelManager, context);
		compiler.compile();
	}
	
	private TrackCompiler(ParsedSong parsedSong, CompiledSong compiledSong, ChannelManager channelManager, CompilerContext context) {
		this.parsedSong = parsedSong;
		this.compiledSong = compiledSong;
		this.channelManager = channelManager;
		this.context = context;

		this.patterns = parsedSong.getPatterns().stream()
				.collect(Collectors.toMap(
						Pattern::getName, (pattern) -> pattern));
	}
	
	public void compile() throws CompileException {
		Track track = new Track(compiledSong, channelManager);
		
		// For now each and every Track starts on the start of the song.
		// Initialize context with all track-related properties.
		context.tick = 0;

		PatternCompiler patternCompiler = new PatternCompiler(track, parsedSong);
		
		var functionCalls = new ArrayList<ContextAwareFunctionCall>();
		
		for(Event event : parsedSong.getTrack().getEvents()) {
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
			compileFunctionCall(track, call.getTick(), call.getFunctionCall());
		}
	}
	
	private void compileFunctionCall(Track track, int tick, FunctionCall functionCall) throws CompileException {

		switch(functionCall.getFunctionName()) {
			case "fade-in":
				FadeInFunction fadeInFunction = new FadeInFunction();
				fadeInFunction.execute(track, tick, functionCall.getArguments());
				break;
			case "fade-out":
				FadeOutFunction fadeOutFunction = new FadeOutFunction();
				fadeOutFunction.execute(track, tick,functionCall.getArguments());
				break;
			default:
				throw new CompileException("Unknown function: " + functionCall.getFunctionName());
		}
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

package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.FunctionParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsedFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.EventType;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.PlayPattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Track;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function.FadeInParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function.FadeOutParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function.TrackFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.function.TrackFunctionParser;

import static nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.ValueParser.parseInteger;

public class TrackBlockParser extends BlockParser<Track> {
	private final SongHeader header;
	
	public TrackBlockParser(SongHeader header, LineParser lineParser, Supplier<Track> defaultTrackSupplier) {
		super(lineParser, defaultTrackSupplier);

		this.header = header;
	}

	@Override
	public Track parse() throws IOException {
		final List<Event> events = new ArrayList<>();

		structureParser.readContentOfBlock(line -> {
			Scanner scanner = new Scanner(line.getRawLine());
			while(scanner.hasNext()) {
				final String inputToken = scanner.next();

				final EventTypeCheckResult examineResult = determineEventType(inputToken, scanner, line.getLineNumber());

				switch(examineResult.getEventType()) {
					case PLAY_PATTERN:
						PlayPattern pattern = parsePlayPatternEvent(inputToken);
						events.add(pattern);
						break;
					case FUNCTION_CALL:
						FunctionCall functionCall = parseFunctionCall(examineResult.getFunction(), line.getLineNumber());
						events.add(functionCall);
						break;
					default:
						throw new IllegalStateException("Unsupported event \""
								+ examineResult.getEventType()
								+ "\" encountered at line "
								+ line.getLineNumber());
				}
			}
		});
		
		Track track = defaultValueSupplier.get();
		track.setEvents(events);
		
		return track;
	}

	private EventTypeCheckResult determineEventType(String inputToken, Scanner scanner, long lineNumber) throws ParseException {
		FunctionParser functionParser = new FunctionParser(scanner);
		
		ParsableFunction function = functionParser.parse(inputToken, lineNumber);
		if(function != null) {
			return new EventTypeCheckResult(EventType.FUNCTION_CALL, function);
		}
		
		return new EventTypeCheckResult(EventType.PLAY_PATTERN, null); 
	}

	private PlayPattern parsePlayPatternEvent(String inputToken) throws ParseException {
		// TODO validate pattern name
		int repeatTimes = 1;

		final int asteriskIndex = inputToken.indexOf('*');
		if(asteriskIndex > 0) {
			repeatTimes = parseInteger(inputToken.substring(asteriskIndex + 1), lineParser.getLineNumber());

			if(repeatTimes < 0) {
				throw new ParseException("The amount of times specified in \"" + inputToken + "\" must be 0 or higher at line " + lineParser.getLineNumber());
			}

			inputToken = inputToken.substring(0, asteriskIndex);
		}

		var playPattern = new PlayPattern(inputToken, repeatTimes);

		return playPattern;
	}
	
	private FunctionCall parseFunctionCall(ParsableFunction function, long lineNumber) throws ParseException {
		TrackFunctionParser trackFunctionParser = null;

		switch(function.getName()) {
			case "fade-in":
				trackFunctionParser = new FadeInParser(function, header, lineNumber);
				break;
			case "fade-out":
				trackFunctionParser = new FadeOutParser(function, header, lineNumber);
				break;
			default:
				throw new ParseException("Encountered unknown track function \"" + function.getName() + "\" on line " + lineParser.getLineNumber());
		}
		
		TrackFunction trackFunction = trackFunctionParser.parse();
		
		return new FunctionCall(trackFunction);
	}

	private static final class EventTypeCheckResult {
		private final EventType eventType;
		private final ParsableFunction function;
		
		public EventTypeCheckResult(EventType eventType, ParsableFunction function) {
			this.eventType = eventType;
			this.function = function;
		}
		
		public EventType getEventType() {
			return eventType;
		}
		
		public ParsableFunction getFunction() {
			return function;
		}
	}
}

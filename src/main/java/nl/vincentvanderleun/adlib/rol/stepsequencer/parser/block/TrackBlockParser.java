package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.ValueParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.EventType;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.PlayPattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Track;

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

				final EventType eventType = determineEventType(inputToken);

				switch(eventType) {
					case PLAY_PATTERN:
						var playPattern = new PlayPattern(inputToken);
						events.add(playPattern);
						break;
					case FUNCTION_CALL:
						FunctionCall functionCall = parseFunctionCall(inputToken, line.getLineNumber());
						events.add(functionCall);
						break;
					default:
						throw new IllegalStateException("Track token not supported by compiler: " + eventType);
				}
			}
		});
		
		Track track = defaultValueSupplier.get();
		track.setEvents(events);
		
		return track;
	}

	private FunctionCall parseFunctionCall(String inputToken, long lineNumber) throws ParseException {
		final int startArgumentsIndex = inputToken.indexOf('(');
		final String functionName = inputToken.substring(0, startArgumentsIndex);
		final String argumentsString = inputToken.substring(startArgumentsIndex + 1, inputToken.length() - 1);
		
		final List<String> arguments = Arrays.asList(argumentsString.split(Pattern.quote(",")));
		
		FunctionCall function;
		switch(functionName) {
			case "fade-in":
			case "fade-out":
				function = parseFadeInOrOutFunction(functionName, arguments, lineNumber);
				break;
			default:
				throw new ParseException("Unknown function \"" + functionName + "\" called at line " + lineNumber);
		}
		
		return function;
	}
	
	private FunctionCall parseFadeInOrOutFunction(String functionName, List<String> arguments, long lineNumber) throws ParseException {
		checkArgumentCount(functionName, arguments, 1, lineNumber);

		int durationTicks = ValueParser.parseNoteDuration(
				arguments.get(0),
				header.getTicksPerBeat(),
				header.getBeatsPerMeasure(),
				lineNumber);
		
		var parsedArguments = new ArrayList<>();
		parsedArguments.add(durationTicks);
		
		return new FunctionCall(functionName, parsedArguments);
	}
	
	private void checkArgumentCount(String functionName, List<String> arguments, int expectedNumberOfArguments, long lineNumber) throws ParseException {
		if(arguments.size() != expectedNumberOfArguments) {
			throw new ParseException("Function " + functionName + " expects " + expectedNumberOfArguments
					+ "arguments , but got " + arguments.size() + " instead at line " + lineNumber);
		}
	}
	
	private EventType determineEventType(String inputToken) {
		if(structureParser.isFunction(inputToken)) {
			return EventType.FUNCTION_CALL;
		}
		return EventType.PLAY_PATTERN;
	}
}

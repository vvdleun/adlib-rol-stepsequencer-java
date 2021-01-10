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
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Event;
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

				final Event event = parseNextEvent(inputToken, scanner, line.getLineNumber());

				events.add(event);
			}
		});
		
		Track track = defaultValueSupplier.get();
		track.setEvents(events);
		
		return track;
	}

	private Event parseNextEvent(String inputToken, Scanner scanner, long lineNumber) throws ParseException {
		Event event = parseFunctionCall(inputToken, scanner, lineNumber);
		if(event != null) {
			return event;
		}
		
		return parsePlayPatternEvent(inputToken); 
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
	
	private FunctionCall parseFunctionCall(String inputToken, Scanner scanner, long lineNumber) throws ParseException {
		FunctionParser functionParser = new FunctionParser(scanner);

		ParsableFunction function = functionParser.parse(inputToken, lineNumber);
		if(function == null) {
			return null;
		}

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
}

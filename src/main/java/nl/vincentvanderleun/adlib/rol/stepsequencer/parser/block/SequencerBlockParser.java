package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.EventType;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.PlayPattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Sequencer;

public class SequencerBlockParser extends BlockParser<Sequencer> {
	
	public SequencerBlockParser(LineParser lineParser, Supplier<Sequencer> defaultSequencerSupplier) {
		super(lineParser, defaultSequencerSupplier);
	}

	@Override
	public Sequencer parse() throws IOException {
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
						FunctionCall functionCall = parseFunctionCall(inputToken);
						events.add(functionCall);
						break;
					default:
						throw new IllegalStateException("Sequencer token not supported by compiler: " + eventType);
				}
			}
		});
		
		Sequencer sequencer = defaultValueSupplier.get();
		sequencer.setEvents(events);
		
		return sequencer;
	}

	private FunctionCall parseFunctionCall(String inputToken) {
		final int startArgumentsIndex = inputToken.indexOf('(');
		final String functionName = inputToken.substring(0, startArgumentsIndex);
		final String argumentsString = inputToken.substring(startArgumentsIndex + 1, inputToken.length() - 1);
		
		final List<String> arguments = Arrays.asList(argumentsString.split(Pattern.quote(",")));
		
		return new FunctionCall(functionName, arguments);
	}
	
	private EventType determineEventType(String inputToken) {
		if(structureParser.isFunction(inputToken)) {
			return EventType.FUNCTION_CALL;
		}
		return EventType.PLAY_PATTERN;
	}
}

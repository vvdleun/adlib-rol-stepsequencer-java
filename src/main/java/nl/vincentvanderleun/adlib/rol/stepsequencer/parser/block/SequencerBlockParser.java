package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.sequencer.EventType;
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
						PlayPattern playPattern = new PlayPattern(inputToken);
						events.add(playPattern);
						break;
					case FUNCTION:
						System.out.println("Sequencer event ignored during parsing for now: " + eventType);
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

	private EventType determineEventType(String inputToken) {
		if(structureParser.isFunction(inputToken)) {
			return EventType.FUNCTION;
		}
		return EventType.PLAY_PATTERN;
	}
}

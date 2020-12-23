package nl.vincentvanderleun.adlib.rol.monosynth.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Event;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.Sequencer;
import nl.vincentvanderleun.adlib.rol.monosynth.song.sequencer.event.PlayPattern;

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

				final SequencerTokenType tokenType = determineTypeOfToken(inputToken);

				switch(tokenType) {
					case PATTERN:
						PlayPattern playPattern = new PlayPattern(inputToken);
						events.add(playPattern);
						break;
					default:
						System.out.println("IGNORED, NOT IMPLEMENTED YET: " + inputToken);
						break;
				}
			}
		});
		
		Sequencer sequencer = defaultValueSupplier.get();
		sequencer.setEvents(events);
		
		return sequencer;
	}

	private SequencerTokenType determineTypeOfToken(String inputToken) {
		if(structureParser.isFunction(inputToken)) {
			return SequencerTokenType.FUNCTION;
		}
		return SequencerTokenType.PATTERN;
	}
	
	private enum SequencerTokenType {
		PATTERN,
		FUNCTION
	}
}

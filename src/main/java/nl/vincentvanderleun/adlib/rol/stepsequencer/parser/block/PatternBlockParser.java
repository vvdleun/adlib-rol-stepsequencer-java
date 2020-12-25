package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.NoteValue;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.BlockFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Hold;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Note;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.OctaveChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.PatchChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;

public class PatternBlockParser extends BlockParser<Pattern> {
	private static final Map<String, NoteValue> NOTES = new HashMap<>(12);

	private final String patternName;

	static {
		NOTES.put("C", NoteValue.C);
		NOTES.put("C#", NoteValue.C_SHARP);
		NOTES.put("D", NoteValue.D);
		NOTES.put("D#", NoteValue.D_SHARP);
		NOTES.put("E", NoteValue.E);
		NOTES.put("F", NoteValue.F);
		NOTES.put("F#", NoteValue.F_SHARP);
		NOTES.put("G", NoteValue.G);
		NOTES.put("G#", NoteValue.G_SHARP);
		NOTES.put("A", NoteValue.A);
		NOTES.put("A#", NoteValue.A_SHARP);
		NOTES.put("B", NoteValue.B);
	}

	public PatternBlockParser(String patternName, LineParser lineParser, Supplier<Pattern> defaultValueSupplier) {
		super(lineParser, defaultValueSupplier);

		this.patternName = patternName;
	}
	
	@Override
	public Pattern parse() throws IOException {
		if(patternName == null || patternName.isEmpty()) {
			throw new ParseException("No name specified in pattern at line " + lineParser.getLineNumber());
		}

		final List<Event> events = new ArrayList<>();

		structureParser.readContentOfBlock((line) -> {
			Scanner scanner = new Scanner(line.getRawLine());

			while(scanner.hasNext()) {
				final String inputToken = scanner.next();

				final PatternTokenType tokenType = determineTypeOfToken(inputToken);
				
				switch(tokenType) {
					case NOTE:
						Note note = parseNote(inputToken);
						events.add(note);
						break;
					case REST:
						events.add(new Rest());
						break;
					case HOLD:
						events.add(new Hold());
						break;
					case FUNCTION:
						Event functionEvent = parseFunction(inputToken);
						events.add(functionEvent);
						break;
					default:
						throw new ParseException("Cannot parse sequence token \"" + inputToken + "\" at line " + lineParser.getLineNumber());
				}
			}
		});

		Pattern pattern = defaultValueSupplier.get();
		pattern.setName(patternName);
		pattern.setEvents(events);
		
		return pattern;
	}

	private Event parseFunction(String inputToken) throws ParseException {
		BlockFunction function = structureParser.parseFunction(inputToken);		
		switch(function.getName()) {
			case "patch":
				return new PatchChange(function.getArgument(0));
			case "octave":
				return new OctaveChange(function.parseArgumentAsInteger(0));
			default:
				throw new ParseException("Encountered unknown command \"" + inputToken + "\" on line " + lineParser.getLineNumber());
		}
	}
	
	private PatternTokenType determineTypeOfToken(String inputToken) {
		if(inputToken.equals("-")) {
			return PatternTokenType.REST;
		} else if(inputToken.equals("h")) {
			return PatternTokenType.HOLD;
		}

		if(structureParser.isFunction(inputToken)) {
			return PatternTokenType.FUNCTION;
		}

		if(isNote(inputToken)) {
			return PatternTokenType.NOTE;
		}
		
		return PatternTokenType.UNKNOWN;
	}
	
	private boolean isNote(String inputToken) {
		return parseNote(inputToken) != null;
	}
	
	private Note parseNote(String inputToken) {
		int offsetOctave = 0;
		
		while(inputToken.startsWith("+")) {
			offsetOctave++;
			inputToken = inputToken.substring(1);
		}

		while(inputToken.startsWith("-")) {
			offsetOctave--;
			inputToken = inputToken.substring(1);
		}

		NoteValue noteValue = NOTES.get(inputToken);

		if(noteValue == null) {
			return null;
		}
		return new Note(noteValue, 1, offsetOctave);
	}

	private enum PatternTokenType {
		NOTE,
		REST,
		HOLD,
		FUNCTION,
		UNKNOWN
	}
}

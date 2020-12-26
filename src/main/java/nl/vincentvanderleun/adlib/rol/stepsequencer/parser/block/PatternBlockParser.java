package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Note;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.BlockFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Hold;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.OctaveChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.PatchChange;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;

public class PatternBlockParser extends BlockParser<Pattern> {
	private static final Map<String, Note> NOTES = new HashMap<>(12);

	private final String patternName;

	static {
		NOTES.put("C", Note.C);
		NOTES.put("C#", Note.C_SHARP);
		NOTES.put("Db", Note.C_SHARP);
		NOTES.put("D", Note.D);
		NOTES.put("D#", Note.D_SHARP);
		NOTES.put("Eb", Note.D_SHARP);
		NOTES.put("E", Note.E);
		NOTES.put("F", Note.F);
		NOTES.put("F#", Note.F_SHARP);
		NOTES.put("Gb", Note.F_SHARP);
		NOTES.put("G", Note.G);
		NOTES.put("G#", Note.G_SHARP);
		NOTES.put("Ab", Note.G_SHARP);
		NOTES.put("A", Note.A);
		NOTES.put("A#", Note.A_SHARP);
		NOTES.put("Bb", Note.A_SHARP);
		NOTES.put("B", Note.B);
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

				// FIXME: everything is parsed twice
				final PatternTokenType tokenType = determineTypeOfToken(inputToken);
				
				switch(tokenType) {
					case NOTE:
						NoteEvent noteEvent = parseNote(inputToken);
						events.add(noteEvent);
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
	
	private NoteEvent parseNote(String inputToken) {
		// Valid notes:
		// "C", "+C", "-C", "+++C", "---C", "C-4", "--C-4", "++C-4"
		int offsetOctave = 0;
		int duration = 1;

		// Remove optional + and - prefixes
		while(inputToken.startsWith("+")) {
			offsetOctave++;
			inputToken = inputToken.substring(1);
		}
		while(inputToken.startsWith("-")) {
			offsetOctave--;
			inputToken = inputToken.substring(1);
		}
		
		// Is a duration specified?
		int dashIndex = inputToken.indexOf('-');
		if(dashIndex > 0) {
			int parsedDuration = parseNoteDuration(inputToken);
			if(parsedDuration > 0) {
				duration = parsedDuration;
				inputToken = inputToken.substring(0, dashIndex);
			}
		}
		
		Note note = NOTES.get(inputToken);

		if(note == null) {
			return null;
		}

		return new NoteEvent(note, duration, offsetOctave);
	}

	private int parseNoteDuration(String note) {
		try {
			String[] splitNote = note.split(java.util.regex.Pattern.quote("-"), 2);
			return Integer.parseInt(splitNote[1]);
		} catch(NumberFormatException ex) {
			// There must be a better way to detect whether a String
			// contains a number, without resorting to reg-ex :'(
			return -1;
		}
	}
	
	private enum PatternTokenType {
		NOTE,
		REST,
		HOLD,
		FUNCTION,
		UNKNOWN
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
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
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pitch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;

public class PatternBlockParser extends BlockParser<Pattern> {
	private static final Map<String, Note> NOTES = new HashMap<>(12);

	private final String patternName;

	private enum PatternTokenType {
		FUNCTION,
		HOLD,
		NOTE,
		PITCH,
		REST,
		UNKNOWN
	}

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

				ParseResult parseResult = parseToken(inputToken);
				
				switch(parseResult.getTokenType()) {
					case FUNCTION:
					case HOLD:
					case NOTE:
					case PITCH:
					case REST:
						events.add(parseResult.getEvent());
						break;
					case UNKNOWN:
					default:
						throw new ParseException("Unknown token \"" + inputToken + "\" at line " + lineParser.getLineNumber());
				}
			}
		});

		Pattern pattern = defaultValueSupplier.get();
		pattern.setName(patternName);
		pattern.setEvents(events);
		
		return pattern;
	}

	private ParseResult parseToken(String inputToken) throws ParseException {
		Event parsedEvent;
		
		parsedEvent = parseRest(inputToken);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.REST, parsedEvent);
		} 

		parsedEvent = parseHold(inputToken);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.HOLD, parsedEvent);
		}

		parsedEvent = parsePitchEvent(inputToken);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.PITCH, parsedEvent);
		}

		if(structureParser.isFunction(inputToken)) {
			Event parsedFunctionEvent = parseFunction(inputToken);
			return new ParseResult(PatternTokenType.FUNCTION, parsedFunctionEvent);
		}
	
		parsedEvent = parseNote(inputToken);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.NOTE, parsedEvent);
		}

		return new ParseResult(PatternTokenType.UNKNOWN, null);
		
	}
	
	private Rest parseRest(String inputToken) {
		return parseEventWithDuration("r", inputToken, (duration) -> new Rest(duration));
	}

	private Hold parseHold(String inputToken) {
		return parseEventWithDuration("h", inputToken, (duration) -> new Hold(duration));
	}
	
	private <T> T parseEventWithDuration(String eventToken, String inputToken, Function<Integer, T> eventSupplier) {
		if(!inputToken.startsWith(eventToken)) {
			return null;
		}

		// Is a duration specified?
		int duration = 1;
		int dashIndex = inputToken.indexOf('-');
		if(dashIndex == eventToken.length()) {
			int parsedDuration = parseDuration(inputToken);
			if(parsedDuration > 0) {
				duration = parsedDuration;
			}
		} else if(dashIndex > eventToken.length()) {
			return null;
		}

		return eventSupplier.apply(duration);
	}
	

	private NoteEvent parseNote(String inputToken) {
		// Valid notes:
		// "C", "+C", "-C", "+++C", "---C", "C-4", "--C-4", "++C-4"
		int offsetOctave = 0;
		int duration = 1;

		// Remove optional + and - prefixes
		while(inputToken.startsWith("+") || inputToken.startsWith("-")) {
			if(inputToken.startsWith("+")) {
				offsetOctave++;
				inputToken = inputToken.substring(1);
			}
			if(inputToken.startsWith("-")) {
				offsetOctave--;
				inputToken = inputToken.substring(1);
			}
		}
		
		// Is a duration specified?
		int dashIndex = inputToken.indexOf('-');
		if(dashIndex > 0) {
			int parsedDuration = parseDuration(inputToken);
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

	private Pitch parsePitchEvent(String inputToken) {
		// Valid: "P1.00"
		if(!inputToken.startsWith("P") || inputToken.length() == 1) {
			return null;
		}
		
		try {
			int duration;
			if(inputToken.contains("-")) {
				 duration = parseDuration(inputToken);
				 inputToken = inputToken.substring(0, inputToken.indexOf('-'));
			} else {
				duration = 1;
			}

			String value = inputToken.substring(1);

			return new Pitch(Float.valueOf(value), duration);
		} catch(NumberFormatException ex) {
			return null;
		}
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

	private int parseDuration(String inputToken) {
		try {
			String[] splitToken = inputToken.split(java.util.regex.Pattern.quote("-"), 2);
			return Integer.parseInt(splitToken[1]);
		} catch(NumberFormatException ex) {
			// There must be a better way to detect whether a String
			// contains a number, without resorting to reg-ex :'(
			return -1;
		}
	}

	private static final class ParseResult {
		private final PatternTokenType tokenType;
		private final Event event;
		
		public ParseResult(PatternTokenType tokenType, Event event) {
			this.tokenType = tokenType;
			this.event = event;
		}
		
		public PatternTokenType getTokenType() {
			return tokenType;
		}
		
		public Event getEvent() {
			return event;
		}
	}
}

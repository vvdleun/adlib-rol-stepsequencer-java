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
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.FunctionParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function.ParsableFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.FunctionCall;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Hold;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pitch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function.OctaveChangeParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function.PatchChangeParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function.PatternFunction;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.function.PatternFunctionParser;

import static nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.ValueParser.parseFloat;
import static nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.ValueParser.parseInteger;

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

				ParseResult parseResult = parseNextToken(inputToken, scanner);
				
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

	private ParseResult parseNextToken(String inputToken, Scanner scanner) throws ParseException {
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

		parsedEvent = parseFunction(inputToken, scanner);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.FUNCTION, parsedEvent);
		}
	
		parsedEvent = parseNote(inputToken);
		if(parsedEvent != null) {
			return new ParseResult(PatternTokenType.NOTE, parsedEvent);
		}

		return new ParseResult(PatternTokenType.UNKNOWN, null);
	}
	
	private Rest parseRest(String inputToken) throws ParseException {
		return parseEventWithDuration("r", inputToken, (duration) -> new Rest(duration));
	}

	private Hold parseHold(String inputToken) throws ParseException {
		return parseEventWithDuration("h", inputToken, (duration) -> new Hold(duration));
	}
	
	private <T> T parseEventWithDuration(String eventToken, String inputToken, java.util.function.Function<Integer, T> eventSupplier) throws ParseException {
		if(!inputToken.startsWith(eventToken)) {
			return null;
		}

		// Is a duration specified?
		int duration = 1;
		int dashIndex = inputToken.indexOf('-');
		if(dashIndex == eventToken.length()) {
			duration = parseDuration(inputToken);
		} else if(dashIndex > eventToken.length()) {
			return null;
		}

		return eventSupplier.apply(duration);
	}

	private NoteEvent parseNote(String inputToken) throws ParseException {
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
			duration = parseDuration(inputToken);
			inputToken = inputToken.substring(0, dashIndex);
		}
		
		Note note = NOTES.get(inputToken);

		if(note == null) {
			return null;
		}

		return new NoteEvent(note, duration, offsetOctave);
	}

	private Pitch parsePitchEvent(String inputToken) throws ParseException {
		// Valid: "P1.00"
		if(!inputToken.startsWith("P") || inputToken.length() == 1) {
			return null;
		}
		
		int duration;
		if(inputToken.contains("-")) {
			 duration = parseDuration(inputToken);
			 inputToken = inputToken.substring(0, inputToken.indexOf('-'));
		} else {
			duration = 1;
		}

		String value = inputToken.substring(1);

		final float parsedFloat = parseFloat(value, lineParser.getLineNumber());
			
		return new Pitch(parsedFloat, duration);
	}

	private int parseDuration(String inputToken) throws ParseException {
		String[] splitToken = inputToken.split(java.util.regex.Pattern.quote("-"), 2);
		int duration = parseInteger(splitToken[1], lineParser.getLineNumber());
		if(duration <= 0) {
			throw new ParseException("Duration specified in \"" + inputToken + "\" must be 1 or higher at line " + lineParser.getLineNumber());
		}
		return duration;
	}

	private FunctionCall parseFunction(String inputToken, Scanner scanner) throws ParseException {
		FunctionParser functionParser = new FunctionParser(scanner);
		
		ParsableFunction parsableFunction = functionParser.parse(inputToken, lineParser.getLineNumber());
		if(parsableFunction == null) {
			return null;
		}

		PatternFunctionParser patternFunctionParser = null;
		switch(parsableFunction.getName()) {
			case "patch":
				patternFunctionParser = new PatchChangeParser(parsableFunction, lineParser.getLineNumber());
				break;
			case "octave":
				patternFunctionParser = new OctaveChangeParser(parsableFunction, lineParser.getLineNumber());
				break;
			default:
				throw new ParseException("Encountered unknown pattern function \"" + inputToken + "\" on line " + lineParser.getLineNumber());
		}
		
		PatternFunction parsedPatternFunction = patternFunctionParser.parse();
		
		return new FunctionCall(parsedPatternFunction);
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

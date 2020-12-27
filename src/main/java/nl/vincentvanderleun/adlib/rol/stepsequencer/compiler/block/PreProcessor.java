package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.block;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.CompileException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.EventType;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Rest;

/**
 * Some events turn out to be easier to handle if they just sneakily directly modify the parsed data
 * before compiling. Line numbers should not be reported back to user after this point.
 *
 * @author Vincent
 *
 */
public class PreProcessor {
	private final ParsedSong parsedSong;

	private int processedEvents;
	
	public static void process(ParsedSong parsedSong) throws CompileException {
		PreProcessor preProcessor = new PreProcessor(parsedSong);
		
		preProcessor.process();
		
		System.out.println(preProcessor.processedEvents + " event(s) pre-processed...");
	}
	
	private PreProcessor(ParsedSong parsedSong) {
		this.parsedSong = parsedSong;
		processedEvents = 0;
	}
	
	public void process() throws CompileException {
		processPatterns();
	}
	
	private void processPatterns() {
		parsedSong.getPatterns().forEach(this::processPattern);
	}
	
	private void processPattern(Pattern pattern) {
		NavigableSet<Integer> eventsToDelete = new TreeSet<>();
		Map<Integer, Event> eventsToReplace = new HashMap<>();

		for(int i = 0; i < pattern.getEvents().size(); i++) {
			final Event event = pattern.getEvents().get(i);

			if(event.getEventType() == EventType.HOLD) {
				processHoldEvent(pattern, i, eventsToDelete, eventsToReplace);
			}
		}

		eventsToReplace.entrySet().forEach((entry) -> {
			System.out.println("Replacing event  " + pattern.getEvents().get((int)entry.getKey()) + " with " + entry.getValue());
			pattern.getEvents().remove((int)entry.getKey());
			pattern.getEvents().add(entry.getKey(), entry.getValue());
		});
		
		eventsToDelete.descendingSet().forEach(index -> {
			System.out.println("Removing event " + pattern.getEvents().get((int)index));
			pattern.getEvents().remove((int)index);
		});
	}
	
	/**
	 * Finds a note earlier in the pattern and increases its duration with 1 tick.
	 * If the pattern does not contain a note event before this event, a new rest is added instead.
	 * The Hold event itself is removed in either case.
	 * @param pattern
	 * @param holdEventIndex
	 */
	private void processHoldEvent(Pattern pattern, int holdEventIndex, NavigableSet<Integer> eventsToDelete, Map<Integer, Event> eventsToReplace) {
		// Find note to hold
		boolean found = false;

		for(int i = holdEventIndex - 1; i >= 0; i--) {
			final Event earlierEvent = pattern.getEvents().get(i);
			if(earlierEvent.getEventType() == EventType.NOTE) {
				found = true;
				System.out.println("Increasing duration of: " + earlierEvent + " to duration +1");
				((NoteEvent)earlierEvent).increaseDuration(1);
				break;
			}
		}

		if (found) {
			eventsToDelete.add(holdEventIndex);
		} else {
			System.out.println("Preprocessor warning: no note found to hold. Replacing with rest instead");
			eventsToReplace.put(holdEventIndex, new Rest());
		}
		
		processedEvents++;
	}
	
}

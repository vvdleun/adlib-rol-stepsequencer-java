package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class makes it very convenient to work with channel-based ROL events, especially notes,
 * as it automatically shortens notes that overlap with newly added notes and delete notes that
 * overlap that were placed after the start tick.
 * 
 * It's heavily opinionated and should ideally have some configurable behavior. 
 *
 * @author Vincent
 *
 */
public class Channel {
	private final NavigableMap<Integer, NoteEvent> notes;
	private final Map<Integer, OtherEvents> otherEvents;	// OtherEvents is a private class defined below
	
	public Channel() {
		this.notes = new TreeMap<>();
		this.otherEvents = new HashMap<>();
	}

	public void addNoteEvent(int tick, NoteEvent note) {
		// Some sanity checks
		validateTick(tick);
		if(note.getDuration() == 0) {
			throw new IllegalArgumentException("Note duration must be > 0");
		}
		
		// Overlapping with an earlier note? That note will be shortened then.
		final Entry<Integer, NoteEvent> lowerEntry = notes.lowerEntry(tick);
		if(lowerEntry != null && getNoteEnd(lowerEntry) >= tick) {
			int newDuration = tick - lowerEntry.getKey();
			lowerEntry.getValue().setDuration(newDuration);
		}
		
		notes.put(tick, note);
		
		// Delete remaining overlapping notes
		Entry<Integer, NoteEvent> nextHigherEntry = notes.higherEntry(tick);
		while(nextHigherEntry != null && nextHigherEntry.getKey() < tick + note.getDuration()) {
			notes.remove(nextHigherEntry.getKey());
			nextHigherEntry = notes.higherEntry(tick);
		}
	}
	
	private int getNoteEnd(Entry<Integer, NoteEvent> noteStart) {
		return noteStart.getKey() + noteStart.getValue().getDuration() - 1;
	}

	public void addInstrumentEvent(int tick, String instrument) {
		validateTick(tick);

		OtherEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.instrument = instrument;
	}

	public void addVolumeEvent(int tick, float volume) {
		validateTick(tick);

		OtherEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.volume = volume;
	}

	public void addPitchEvent(int tick, float pitch) {
		validateTick(tick);

		OtherEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.pitch = pitch;
	}

	private OtherEvents getExistingOrAddEmptyOtherEventAt(int tick) {
		return otherEvents.computeIfAbsent(tick, (key) -> new OtherEvents());
	}

	private void validateTick(int tick) {
		if(tick < 0 || tick > 65535) {
			throw new IllegalArgumentException("Tick must be between 0 and 65535 (both inclusive)");
		}
	}

	public ChannelEvents getEventsAtTick(int tick) {
		final NoteEvent noteEvent = notes.get(tick);
		final OtherEvents otherEvents = this.otherEvents.get(tick);

		return new ChannelEvents(
				tick,
				noteEvent,
				otherEvents != null ? otherEvents.instrument : null,
				otherEvents != null ? otherEvents.volume : null,
				otherEvents != null ? otherEvents.pitch : null);
	}

	public Set<ChannelEvents> getAllEvents() {
		Stream<ChannelEvents> streamNoteEvents = notes.entrySet().stream()
				.map((entry) -> eventWithNoteEvenOnly(entry.getKey(), entry.getValue()));

		Stream<ChannelEvents> streamOtherEvents = otherEvents.entrySet().stream()
				.map((entry) -> eventWithOtherEventsOnly(entry.getKey(), entry.getValue()));
		
		return Stream.concat(streamNoteEvents, streamOtherEvents)
				.collect(
						Collectors.collectingAndThen(
								Collectors.toMap(
										ChannelEvents::getTick,
										Function.identity(),
										this::mergeNoteAndOtherEvents), 
								(eventMap) -> new TreeSet<>(eventMap.values())));
	}
	
	private ChannelEvents eventWithNoteEvenOnly(int tick, NoteEvent noteEvent) {
		return new ChannelEvents(
				tick,						// Tick
				noteEvent,					// Note event
				null,						// Instrument
				null,						// Volume
				null);						// Pitch
	}
	
	private ChannelEvents eventWithOtherEventsOnly(int tick, OtherEvents otherEvents) {
		return new ChannelEvents(
				tick,						// Tick
				null,						// Note event
				otherEvents.instrument,		// Instrument
				otherEvents.volume,			// Volume
				otherEvents.pitch);			// Pitch		
	}
	
	private ChannelEvents mergeNoteAndOtherEvents(ChannelEvents event1, ChannelEvents event2) {
		final ChannelEvents noteEvent = event1.getNote() != null ? event1 : event2;
		final ChannelEvents otherEvents = event1.getNote() == null ? event1 : event2;

		if(noteEvent == otherEvents || event1.getTick() != event2.getTick()) {
			throw new IllegalStateException("Internal error: merge conflict");
		}
		
		return new ChannelEvents(
				event1.getTick(),
				noteEvent.getNote(),
				otherEvents.getInstrument(),
				otherEvents.getVolume(),
				otherEvents.getPitch());
	}

	@Override
	public int hashCode() {
		return Objects.hash(notes, otherEvents);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		return Objects.equals(notes, other.notes) && Objects.equals(otherEvents, other.otherEvents);
	}

	@Override
	public String toString() {
		return "Channel [notes=" + notes + ", otherEvents=" + otherEvents + "]";
	}
	
	// Quick & dirty class to store a selection of the possible ROL events (those that do not require
	// special logic when adding those events) in one data-structure. No getters/setters as this 
	// class is not exposed outside this class.
	
	private static class OtherEvents {
		private String instrument;
		private Float volume;
		private Float pitch;
	}
}

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

public class Channel {
	// Note that "otherEvents" does not store note and instrument events, but hides
	// this fact for the consumer by returning the note in each and every returned ChannelEvents instance.
	private final NavigableMap<Integer, NoteEvent> notes;
	private final Map<Integer, ChannelEvents> otherEvents;
	
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

		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setInstrument(instrument);
	}

	public void addVolumeEvent(int tick, float volume) {
		validateTick(tick);

		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setVolume(volume);
	}

	public void addPitchEvent(int tick, float pitch) {
		validateTick(tick);

		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setPitch(pitch);
	}

	private ChannelEvents getExistingOrAddEmptyOtherEventAt(int tick) {
		return otherEvents.computeIfAbsent(tick, (key) -> ChannelEvents.empty());
	}

	private void validateTick(int tick) {
		if(tick < 0 || tick > 65535) {
			throw new IllegalArgumentException("Tick must be between 0 and 65535 (both inclusive)");
		}
	}

	public ChannelEvents getEventsAtTick(int tick) {
		final NoteEvent noteEvent = notes.get(tick);
		final ChannelEvents otherEvents = this.otherEvents.get(tick);

		if(noteEvent == null && otherEvents == null) {
			return new ChannelEvents(null, null, null, null);
		}

		return new ChannelEvents(
				noteEvent,
				otherEvents != null ? otherEvents.getInstrument() : null,
				otherEvents != null ? otherEvents.getVolume() : null,
				otherEvents != null ? otherEvents.getPitch() : null);
	}

	public Set<TickChannelEvents> getAllEvents() {
		Stream<TickChannelEvents> streamNoteEvents = notes.entrySet().stream()
				.map((entry) -> new TickChannelEvents(
						entry.getKey(),
						ChannelEvents.fromNote(entry.getValue())));

		Stream<TickChannelEvents> streamOtherEvents = otherEvents.entrySet().stream()
				.map((entry) -> new TickChannelEvents(
						entry.getKey(),
						entry.getValue()));
		
		return Stream.concat(streamNoteEvents, streamOtherEvents)
				.collect(
						Collectors.collectingAndThen(
								Collectors.toMap(
										TickChannelEvents::getTick,
										Function.identity(),
										this::mergeNoteAndOtherEvents), 
								(eventMap) -> new TreeSet<>(eventMap.values())));
	}
	
	private TickChannelEvents mergeNoteAndOtherEvents(TickChannelEvents event1, TickChannelEvents event2) {
		final TickChannelEvents noteEvent = event1.getChannelEvents().getNote() != null ? event1 : event2;
		final TickChannelEvents otherEvent = event1.getChannelEvents().getNote() == null ? event1 : event2;

		if(noteEvent == otherEvent || event1.getTick() != event2.getTick()) {
			throw new IllegalStateException("Internal error: merge conflict");
		}
		
		var mergedChannelEvent = new ChannelEvents(
				noteEvent.getChannelEvents().getNote(),
				otherEvent.getChannelEvents().getInstrument(),
				otherEvent.getChannelEvents().getVolume(),
				otherEvent.getChannelEvents().getPitch());

		return new TickChannelEvents(event1.getTick(), mergedChannelEvent);
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
}

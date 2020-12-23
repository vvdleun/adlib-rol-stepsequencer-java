package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Channel {
	private final MonophonicNoteEvents notes;
	// Note that "otherEvents" does not store note events, but hides this fact for the consumer
	// by returning the note in each and every returned ChannelEvents instance.
	private final Map<Integer, ChannelEvents> otherEvents;
	
	public Channel() {
		this.notes = new MonophonicNoteEvents();
		this.otherEvents = new HashMap<>();
	}

	public void addNoteEvent(int tick, NoteEvent note) {
		notes.placeNote(tick, note);
	}
	
	public void addInstrumentEvent(int tick, String instrument) {
		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setInstrument(instrument);
	}

	public void addVolumeEvent(int tick, float volume) {
		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setVolume(volume);
	}

	public void addPitchEvent(int tick, float pitch) {
		ChannelEvents otherEvent = getExistingOrAddEmptyOtherEventAt(tick);
		otherEvent.setPitch(pitch);
	}

	private ChannelEvents getExistingOrAddEmptyOtherEventAt(int tick) {
		return otherEvents.computeIfAbsent(tick, (key) -> ChannelEvents.empty());
	}

	public ChannelEvents getEventsAtTick(int tick) {
		Optional<NoteEvent> noteEvent = notes.getNoteThatStartsAt(tick);
		ChannelEvents otherEvents = this.otherEvents.get(tick);
		
		if(noteEvent.isEmpty() && otherEvents == null) {
			return new ChannelEvents(null, null, null, null);
		}
		
		return new ChannelEvents(
				noteEvent.orElse(null),
				otherEvents != null ? otherEvents.getInstrument() : null,
				otherEvents != null ? otherEvents.getVolume() : null,
				otherEvents != null ? otherEvents.getPitch() : null);
	}

	public Set<TickChannelEvents> getEvents() {
		// TODO would be more efficient if the inputs would be Stream<Map<...>.Entry<Integer, NoteEvent>>
		// and Stream<Map<...>.Entry<Integer, ChannelEvents>>
		Stream<TickChannelEvents> streamNoteEvents = notes.streamNotes();

		Stream<TickChannelEvents> streamOtherEvents = otherEvents.entrySet().stream()
				.map((entry) -> new TickChannelEvents(entry.getKey(), entry.getValue()));
		
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

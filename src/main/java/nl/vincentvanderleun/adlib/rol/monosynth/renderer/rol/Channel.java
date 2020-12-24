package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class aims to make it convenient to work with channel-based ROL events. It tries to fix conflicting
 * events (for example: when placing a note that overlaps with other notes, it will shorten the note that
 * preceded the note and delete conflicting notes that succeed it). For non-note events, it won't add events
 * if it sees that exactly the same event preceded it and/or will delete events succeeding it, if those are
 * exactly the same. It tries hard to create normalized collection of events, with no redundant events where possible.
 * 
 * It's heavily opinionated and should ideally have some configurable behavior. 
 *
 * @author Vincent
 *
 */
public class Channel {
	private final NavigableMap<Integer, NoteEvent> notes;
	private final NormalizedEvents<String> instruments;
	private final NormalizedEvents<Float> volumes;
	private final NormalizedEvents<Float> pitches;
	
	public Channel() {
		this.notes = new TreeMap<>();
		this.instruments = new NormalizedEvents<>();
		this.volumes = new NormalizedEvents<>();
		this.pitches = new NormalizedEvents<>();
	}

	public void addNoteEvent(int tick, NoteEvent note) {
		// Some sanity checks
		validateTick(tick);
		if(note == null) {
			throw new NullPointerException("Note event cannot be null");
		}
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

		instruments.add(tick, instrument);
	}

	public void addVolumeEvent(int tick, float volume) {
		validateTick(tick);

		volumes.add(tick,  volume);
	}

	public void addPitchEvent(int tick, float pitch) {
		validateTick(tick);

		pitches.add(tick, pitch);
	}

	private void validateTick(int tick) {
		if(tick < 0 || tick > 65535) {
			throw new IllegalArgumentException("Tick must be between 0 and 65535 (both inclusive)");
		}
	}

	public ChannelEvents getEventsAtTick(int tick) {
		final NoteEvent noteEvent = notes.get(tick);
		final String instrument = instruments.get(tick);
		final Float volume = volumes.get(tick);
		final Float pitch = pitches.get(tick);

		return ChannelEvents.from(tick, noteEvent, instrument, volume, pitch);
	}

	public Set<ChannelEvents> getAllEvents() {
		Stream<ChannelEvents> streamNoteEvents = notes.entrySet().stream()
				.map((entry) -> ChannelEvents.fromNoteEvenOnly(entry.getKey(), entry.getValue()));

		Stream<ChannelEvents> streamInstrumentEvents = instruments.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromInstrumentOnly(entry.getKey(), entry.getValue()));

		Stream<ChannelEvents> streamVolumeEvents = volumes.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromVolumeOnly(entry.getKey(), entry.getValue()));

		Stream<ChannelEvents> streamPitchEvents = pitches.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromPitchOnly(entry.getKey(), entry.getValue()));
		
		return Stream.of(streamNoteEvents, streamInstrumentEvents, streamVolumeEvents, streamPitchEvents)
				.flatMap(events -> events)
				.collect(
						Collectors.collectingAndThen(
								Collectors.toMap(
										ChannelEvents::getTick,
										Function.identity(),
										this::mergeNoteAndOtherEvents), 
								(eventMap) -> new TreeSet<>(eventMap.values())));
	}
	
	private ChannelEvents mergeNoteAndOtherEvents(ChannelEvents event1, ChannelEvents event2) {
		return ChannelEvents.from(
				event1.getTick(),
				event1.getNote() != null ? event1.getNote() : event2.getNote(),
				event1.getInstrument() != null ? event1.getInstrument() : event2.getInstrument(),
				event1.getVolume() != null ? event1.getVolume() : event2.getVolume(),
				event1.getPitch() != null ? event1.getPitch() : event2.getPitch());
	}
}

package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.Event;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.InstrumentEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;

/**
 * This class aims to make it convenient to work with monophonic channels. It tries to automatically fix conflicting
 * events (for example: when placing a note that overlaps with other notes, it will shorten the note that preceded the note
 * and delete conflicting notes that succeed it). For non-note events, it won't add events if it sees that exactly the same
 * event preceded it and/or will delete the event succeeding it, if it were duplicates. It tries hard to create normalized
 * collection of events, with no redundant events where possible.
 * 
 * It's heavily opinionated and should ideally have some configurable behavior. 
 *
 * @author Vincent
 */
public class Channel {
	private final int channel;
	private final NavigableMap<Integer, NoteEvent> notes;
	private final NormalizedEventMap<InstrumentEvent> instruments;
	private final NormalizedEventMap<VolumeMultiplierEvent> volumes;
	private final NormalizedEventMap<PitchMultiplierEvent> pitches;
	
	public Channel(int channel) {
		this.channel = channel;
		this.notes = new TreeMap<>();
		this.instruments = new NormalizedEventMap<>();
		this.volumes = new NormalizedEventMap<>();
		this.pitches = new NormalizedEventMap<>();
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

	public void addEvent(int tick, Event event) {
		switch(event.getEventType()) {
			case INSTRUMENT:
				addInstrumentEvent(tick, (InstrumentEvent)event);
				break;
			case NOTE:
				addNoteEvent(tick, (NoteEvent)event);
				break;
			case PITCH:
				addPitchEvent(tick, (PitchMultiplierEvent)event);
				break;
			case VOLUME:
				addVolumeEvent(tick, (VolumeMultiplierEvent)event);
				break;
			case TEMPO:
			default:
				throw new IllegalStateException("Internal error: event " + event + " not supported for channels by compiler");
		}
	}
	
	public void addInstrumentEvent(int tick, InstrumentEvent event) {
		validateTick(tick);

		instruments.add(tick, event);
	}

	public void addVolumeEvent(int tick, VolumeMultiplierEvent event) {
		validateTick(tick);

		volumes.add(tick,  event);
	}

	public void addPitchEvent(int tick, PitchMultiplierEvent event) {
		validateTick(tick);

		pitches.add(tick, event);
	}

	private void validateTick(int tick) {
		if(tick < 0 || tick > 65535) {
			throw new IllegalArgumentException("Tick must be between 0 and 65535 (both inclusive)");
		}
	}

	public ChannelEvents getEventsAtTick(int tick) {
		final NoteEvent noteEvent = notes.get(tick);
		final String instrument = instruments.get(tick) != null ? instruments.get(tick).getInstrument() : null;
		final Float volume = volumes.get(tick) != null ? volumes.get(tick).getMulitplier() : null;
		final Float pitch = pitches.get(tick) != null ? pitches.get(tick).getMulitplier() : null;

		return ChannelEvents.fromAllEvents(channel, tick, noteEvent, instrument, volume, pitch);
	}

	public Set<ChannelEvents> getAllEvents() {
		Stream<ChannelEvents> streamNoteEvents = notes.entrySet().stream()
				.map((entry) -> ChannelEvents.fromNoteEvenOnly(channel, entry.getKey(), entry.getValue()));

		Stream<ChannelEvents> streamInstrumentEvents = instruments.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromInstrumentOnly(channel, entry.getKey(), entry.getValue().getInstrument()));

		Stream<ChannelEvents> streamVolumeEvents = volumes.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromVolumeOnly(channel, entry.getKey(), entry.getValue().getMulitplier()));

		Stream<ChannelEvents> streamPitchEvents = pitches.getMap().entrySet().stream()
				.map((entry) -> ChannelEvents.fromPitchOnly(channel, entry.getKey(), entry.getValue().getMulitplier()));
		
		return Stream.of(
						streamNoteEvents,
						streamInstrumentEvents,
						streamVolumeEvents,
						streamPitchEvents)
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
		return ChannelEvents.fromAllEvents(
				channel,
				event1.getTick(),
				event1.getNote() != null ? event1.getNote() : event2.getNote(),
				event1.getInstrument() != null ? event1.getInstrument() : event2.getInstrument(),
				event1.getVolume() != null ? event1.getVolume() : event2.getVolume(),
				event1.getPitch() != null ? event1.getPitch() : event2.getPitch());
	}

	@Override
	public int hashCode() {
		return Objects.hash(instruments, notes, pitches, volumes);
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
		return Objects.equals(instruments, other.instruments) && Objects.equals(notes, other.notes)
				&& Objects.equals(pitches, other.pitches) && Objects.equals(volumes, other.volumes);
	}

	@Override
	public String toString() {
		return "Channel [notes=" + notes + ", instruments=" + instruments + ", volumes=" + volumes + ", pitches="
				+ pitches + "]";
	}
}

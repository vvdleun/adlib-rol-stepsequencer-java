package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that knows how to deal with monophonic voice logic. If an existing note's end tick overlaps
 * with the specified tick, then that note is shortened (or replaced entirely if the new note starts on
 * the same tick). Other notes that were added previously and that overlap with the newly added note
 * are automatically deleted.
 * 
 * Currently tightly coupled to the ROL renderer structure. Could be worthwhile to generalize this,
 * as I'll need it in more projects down the road...
 * 
 * @author Vincent 
 */
public class MonophonicNoteEvents {
	private final NavigableMap<Integer, NoteEvent> notes;
	
	public MonophonicNoteEvents() {
		notes = new TreeMap<>();
	}
	
	public void placeNote(int tick, NoteEvent note) {
		// Some sanity checks
		if(note.getDuration() == 0) {
			throw new IllegalArgumentException("Note duration must be > 0");
		}
		if(tick < 0) {
			throw new IllegalArgumentException("Tick must be >= 0");
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

	public Optional<NoteEvent> getNoteThatStartsAt(int tick) {
		return Optional.ofNullable(notes.get(tick));
	}
	
	public Optional<TickChannelEvents> getNoteAt(int tick) {
		Entry<Integer, NoteEvent> floorEntry = notes.floorEntry(tick);
		if(floorEntry != null && getNoteEnd(floorEntry) >= tick) {
			return Optional.of(toTickChannelsEvent(tick, floorEntry.getValue()));
		}
		
		return Optional.empty();
	}
	
	public int getNoteCount() {
		return notes.size();
	}
	
	public List<TickChannelEvents> getNotes() {
		return streamNotes().collect(Collectors.toList());
	}
	
	Stream<TickChannelEvents> streamNotes() {
		return notes.entrySet().stream()
				.map((entry) -> toTickChannelsEvent(entry.getKey(), entry.getValue()));
	}

	private TickChannelEvents toTickChannelsEvent(int tick, NoteEvent note) {
		return new TickChannelEvents(tick, ChannelEvents.fromNote(note));
	}

	@Override
	public int hashCode() {
		return Objects.hash(notes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonophonicNoteEvents other = (MonophonicNoteEvents) obj;
		return Objects.equals(notes, other.notes);
	}

	@Override
	public String toString() {
		return "MonophonicNoteEvents [notes=" + notes + "]";
	}
 }

package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ChannelTests {
	private static final int SOME_NOTE = 100;
	private static final int OTHER_NOTE = 200;
	private static NoteEvent SOME_NOTE_EVENT = new NoteEvent(SOME_NOTE, 1);
	private static NoteEvent OTHER_NOTE_EVENT = new NoteEvent(OTHER_NOTE, 1);
	
	private final Channel channel = new Channel();

	@Test
	public void shouldReturnEmptyEventWhenThereAreNoEventsAddedToTheTick() {
		TickChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertNull(channelEvents.getNote());
		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getPitch());
		assertNull(channelEvents.getVolume());
	}

	@Test
	public void shouldAddNonNoteEventsToATick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		
		TickChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getPitch());
		assertNull(channelEvents.getVolume());
	}

	@Test
	public void shouldAddOtherEventsToATick() {
		channel.addInstrumentEvent(0, "PIANO1");
		channel.addPitchEvent(0, 1.0f);
		channel.addVolumeEvent(0, 0.75f);
		
		TickChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
		assertNull(channelEvents.getNote());
	}
	
	@Test
	public void shouldAddAllEventsAddedToSameTick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		channel.addInstrumentEvent(0, "PIANO1");
		channel.addPitchEvent(0, 1.0f);
		channel.addVolumeEvent(0, 0.75f);
		
		TickChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
	}
	
	@Test
	public void shouldOnlyReturnNoteEventIfNoteStartsAtSpecifiedTick() {
		channel.addNoteEvent(0, new NoteEvent(100, 10)); // 0..9
		
		TickChannelEvents channelEventsAtTick1 = channel.getEventsAtTick(1);
		
		assertEquals(1, channelEventsAtTick1.getTick());
		assertNull(channelEventsAtTick1.getNote());
	}

	@Test
	public void shouldAddNonOverlappingNotes() {
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 1)); // Spans tick 0
		channel.addNoteEvent(1, new NoteEvent(SOME_NOTE, 2)); // Spans tick 1 and 2
		channel.addNoteEvent(3, new NoteEvent(SOME_NOTE, 3)); // Spans tick 3, 4 and 5
		
		TickChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		TickChannelEvents channelEventsAtTick1 = channel.getEventsAtTick(1);
		TickChannelEvents channelEventsAtTick3 = channel.getEventsAtTick(3);
		
		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getNote();
		NoteEvent noteEventAtTick1 = channelEventsAtTick1.getNote();
		NoteEvent noteEventAtTick3 = channelEventsAtTick3.getNote();
		
		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		assertEquals(1, noteEventAtTick0.getDuration());

		assertEquals(1, channelEventsAtTick1.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick1.getNote());
		assertEquals(2, noteEventAtTick1.getDuration());

		assertEquals(3, channelEventsAtTick3.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick3.getNote());
		assertEquals(3, noteEventAtTick3.getDuration());
	}
	
	@Test
	public void shouldShortenOverlappingNotePrecedingAddedNote() {
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2
		channel.addNoteEvent(2, new NoteEvent(SOME_NOTE, 1));

		TickChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		TickChannelEvents channelEventsAtTick2 = channel.getEventsAtTick(2);

		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getNote();
		NoteEvent noteEventAtTick2 = channelEventsAtTick2.getNote();
		
		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		// Note should be shortened from 3 to 2, to make room for the new note
		assertEquals(2, noteEventAtTick0.getDuration());

		assertEquals(2, channelEventsAtTick2.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick2.getNote());
		assertEquals(1, noteEventAtTick2.getDuration());
	}
	
	@Test
	public void shouldDeleteOverlappingNotesFollowingIt() {
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 1));
		channel.addNoteEvent(1, new NoteEvent(SOME_NOTE, 1));
		channel.addNoteEvent(2, new NoteEvent(SOME_NOTE, 1));
		channel.addNoteEvent(3, new NoteEvent(SOME_NOTE, 1));
		
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 3)); // Spans ticks 0, 1 and 2

		TickChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		TickChannelEvents channelEventsAtTick3 = channel.getEventsAtTick(3);

		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getNote();
		NoteEvent noteEventAtTick3 = channelEventsAtTick3.getNote();

		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		assertEquals(3, noteEventAtTick0.getDuration());

		assertEquals(3, channelEventsAtTick3.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick3.getNote());
		assertEquals(1, noteEventAtTick3.getDuration());
	}
	
	@Test
	public void shouldThrowWhenAddingNotesWithNoDuration() {
		assertThrows(IllegalArgumentException.class, () -> {
			channel.addNoteEvent(30, new NoteEvent(SOME_NOTE, 0));
		});
	}

	@Test
	public void shouldThrowWhenAddingNotesOnTickSmallerThanZero() {
		assertThrows(IllegalArgumentException.class, () -> {
			channel.addNoteEvent(-1, new NoteEvent(SOME_NOTE, 10));
		});
	}
	
	@Test
	public void shouldListAllAddedEventsInSortedOrder() {
		// Tick 1
		channel.addNoteEvent(1, SOME_NOTE_EVENT);
		// Tick 0
		channel.addInstrumentEvent(0, "PIANO1");
		// Tick 3
		channel.addNoteEvent(3, OTHER_NOTE_EVENT);
		channel.addInstrumentEvent(3, "PIANO2");
		channel.addVolumeEvent(3, 0.75f);
		channel.addPitchEvent(3, 2.0f);
		// Tick 2
		channel.addNoteEvent(2, OTHER_NOTE_EVENT);
		channel.addVolumeEvent(2, 1.0f);

		Set<TickChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(4, allEventsSet.size());
		
		var allEvents = new ArrayList<>(allEventsSet);
		
		assertEquals(createInstrumentEvent(0, "PIANO1"), allEvents.get(0));
		assertEquals(createNoteEvent(1, SOME_NOTE_EVENT), allEvents.get(1));
		assertEquals(createNoteAndVolumeEvent(2, OTHER_NOTE_EVENT, 1.0f), allEvents.get(2));
		assertEquals(createEvent(3, OTHER_NOTE_EVENT, "PIANO2", 0.75f, 2.0f), allEvents.get(3));
	}
	
	private TickChannelEvents createNoteEvent(int tick, NoteEvent noteEvent) {
		return createEvent(tick, noteEvent, null, null, null);
	}
	
	private TickChannelEvents createInstrumentEvent(int tick, String instrument) {
		return createEvent(tick, null, instrument, null, null);
	}
	
	private TickChannelEvents createNoteAndVolumeEvent(int tick, NoteEvent noteEvent, Float volume) {
		return createEvent(tick, noteEvent, null, volume, null);
	}
	
	private TickChannelEvents createEvent(int tick, NoteEvent noteEvent, String instrument, Float volume, Float pitch) {
		return new TickChannelEvents(tick, noteEvent, instrument, volume, pitch);
	}
}


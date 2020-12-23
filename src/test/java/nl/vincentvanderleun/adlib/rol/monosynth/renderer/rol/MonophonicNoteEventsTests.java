package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class MonophonicNoteEventsTests {
	private static final int SOME_NOTE = 100;
	private final MonophonicNoteEvents monophonicVoice = new MonophonicNoteEvents();
	
	@Test
	public void shouldReturnStoredEventCorrectly() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2
		
		final Optional<TickChannelEvents> channelEvent = monophonicVoice.getNoteAt(0);

		assertTrue(channelEvent.isPresent());

		final TickChannelEvents tickAndChannelEvents = channelEvent.get();

		assertEquals(0, tickAndChannelEvents.getTick());
		
		final ChannelEvents channelEvents = tickAndChannelEvents.getChannelEvents();

		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getVolume());
		assertNull(channelEvents.getPitch());

		final NoteEvent noteEvent = channelEvents.getNote();
		
		assertEquals(SOME_NOTE, noteEvent.getNote());
		assertEquals(3, noteEvent.getDuration());
	}
	
	@Test
	public void shouldReturnNoteThatStartsAtTick() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2

		Optional<TickChannelEvents> channelEvent = monophonicVoice.getNoteAt(0);

		NoteEvent noteEvent = channelEvent.get().getChannelEvents().getNote();

		assertEquals(0, channelEvent.get().getTick());
		assertEquals(SOME_NOTE, noteEvent.getNote());
		assertEquals(3, noteEvent.getDuration());
		
		assertEquals(1, monophonicVoice.getNoteCount());
	}

	@Test
	public void shouldReturnNoteThatOverlapsAtTick() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2

		Optional<TickChannelEvents> channelEvent = monophonicVoice.getNoteAt(0);

		NoteEvent noteEvent = channelEvent.get().getChannelEvents().getNote();

		assertEquals(0, channelEvent.get().getTick());
		assertEquals(SOME_NOTE, noteEvent.getNote());
		assertEquals(3, noteEvent.getDuration());

		assertEquals(1, monophonicVoice.getNoteCount());
	}
	
	@Test
	public void shouldReturnNoteThatEndsAtTick() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2

		Optional<TickChannelEvents> channelEvent = monophonicVoice.getNoteAt(0);

		NoteEvent noteEvent = channelEvent.get().getChannelEvents().getNote();

		assertEquals(0, channelEvent.get().getTick());
		assertEquals(SOME_NOTE, noteEvent.getNote());
		assertEquals(3, noteEvent.getDuration());

		assertEquals(1, monophonicVoice.getNoteCount());
	}
	
	@Test
	public void shouldRecognizeThatThereIsNoNoteAtTick() {
		monophonicVoice.placeNote(8, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(10, new NoteEvent(SOME_NOTE, 3)); // Spans tick 10, 11, 12

		Optional<TickChannelEvents> noteAtTick7 = monophonicVoice.getNoteAt(7);
		Optional<TickChannelEvents> noteAtTick9 = monophonicVoice.getNoteAt(9);
		Optional<TickChannelEvents> noteAtTick13 = monophonicVoice.getNoteAt(13);

		assertTrue(noteAtTick7.isEmpty());
		assertTrue(noteAtTick9.isEmpty());
		assertTrue(noteAtTick13.isEmpty());

		assertEquals(2, monophonicVoice.getNoteCount());
	}

	@Test
	public void shouldAddNonOverlappingNotes() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 1)); // Spans tick 0
		monophonicVoice.placeNote(1, new NoteEvent(SOME_NOTE, 2)); // Spans tick 1 and 2
		monophonicVoice.placeNote(3, new NoteEvent(SOME_NOTE, 3)); // Spans tick 3, 4 and 5
		
		TickChannelEvents channelEventsAtTick0 = monophonicVoice.getNoteAt(0).get();
		TickChannelEvents channelEventsAtTick1 = monophonicVoice.getNoteAt(1).get();
		TickChannelEvents channelEventsAtTick3 = monophonicVoice.getNoteAt(3).get();
		
		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getChannelEvents().getNote();
		NoteEvent noteEventAtTick1 = channelEventsAtTick1.getChannelEvents().getNote();
		NoteEvent noteEventAtTick3 = channelEventsAtTick3.getChannelEvents().getNote();
		
		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		assertEquals(1, noteEventAtTick0.getDuration());

		assertEquals(1, channelEventsAtTick1.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick1.getNote());
		assertEquals(2, noteEventAtTick1.getDuration());

		assertEquals(3, channelEventsAtTick3.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick3.getNote());
		assertEquals(3, noteEventAtTick3.getDuration());

		assertEquals(3, monophonicVoice.getNoteCount());
	}
	
	@Test
	public void shouldShortenOverlappingNotePrecedingAddedNote() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans tick 0, 1, 2
		monophonicVoice.placeNote(2, new NoteEvent(SOME_NOTE, 1));
		
		TickChannelEvents channelEventsAtTick0 = monophonicVoice.getNoteAt(0).get();
		TickChannelEvents channelEventsAtTick2 = monophonicVoice.getNoteAt(2).get();
		
		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getChannelEvents().getNote();
		NoteEvent noteEventAtTick2 = channelEventsAtTick2.getChannelEvents().getNote();
		
		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		// Note should be shortened from 3 to 2, to make room for the new note
		assertEquals(2, noteEventAtTick0.getDuration());

		assertEquals(2, channelEventsAtTick2.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick2.getNote());
		assertEquals(1, noteEventAtTick2.getDuration());

		assertEquals(2, monophonicVoice.getNoteCount());
	}
	
	@Test
	public void shouldDeleteOverlappingNotesFollowingIt() {
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(1, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(2, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(3, new NoteEvent(SOME_NOTE, 1));
		
		monophonicVoice.placeNote(0, new NoteEvent(SOME_NOTE, 3)); // Spans ticks 0, 1 and 2

		TickChannelEvents channelEventsAtTick0 = monophonicVoice.getNoteAt(0).get();
		TickChannelEvents channelEventsAtTick3 = monophonicVoice.getNoteAt(3).get();

		NoteEvent noteEventAtTick0 = channelEventsAtTick0.getChannelEvents().getNote();
		NoteEvent noteEventAtTick3 = channelEventsAtTick3.getChannelEvents().getNote();
				
		assertEquals(0, channelEventsAtTick0.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick0.getNote());
		assertEquals(3, noteEventAtTick0.getDuration());

		assertEquals(3, channelEventsAtTick3.getTick());
		assertEquals(SOME_NOTE, noteEventAtTick3.getNote());
		assertEquals(1, noteEventAtTick3.getDuration());

		assertEquals(2, monophonicVoice.getNoteCount());
	}
	
	@Test
	public void shouldThrowWhenAddingNotesWithNoDuration() {
		assertThrows(IllegalArgumentException.class, () -> {
			monophonicVoice.placeNote(30,  new NoteEvent(SOME_NOTE, 0));
		});
	}

	@Test
	public void shouldThrowWhenAddingNotesOnTickSmallerThanZero() {
		assertThrows(IllegalArgumentException.class, () -> {
			monophonicVoice.placeNote(-1,  new NoteEvent(SOME_NOTE, 10));
		});
	}

	@Test
	public void returnsNotesInSortedOrder() {
		monophonicVoice.placeNote(99, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(101, new NoteEvent(SOME_NOTE, 1));
		monophonicVoice.placeNote(10, new NoteEvent(SOME_NOTE, 1));
		
		List<Integer> notes = monophonicVoice.getNotes().stream()
				.map(TickChannelEvents::getTick)
				.collect(Collectors.toList());

		assertEquals(3, notes.size());

		assertEquals(10, notes.get(0));
		assertEquals(99, notes.get(1));
		assertEquals(101, notes.get(2));
	}
}

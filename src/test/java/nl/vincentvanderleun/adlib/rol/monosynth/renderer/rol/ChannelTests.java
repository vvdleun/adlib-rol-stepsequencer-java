package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class ChannelTests {
	private static NoteEvent SOME_NOTE_EVENT = new NoteEvent(100, 1);
	private static NoteEvent OTHER_NOTE_EVENT = new NoteEvent(200, 1);
	
	private final Channel channel = new Channel();

	@Test
	public void shouldReturnEmptyEventWhenThereAreNoEventsAddedToTheTick() {
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertNull(channelEvents.getNote());
		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getPitch());
		assertNull(channelEvents.getVolume());
	}

	@Test
	public void shouldAddNoteEventToATick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
	}

	@Test
	public void shouldAddOtherEventsToATick() {
		channel.addInstrumentEvent(0, "PIANO1");
		channel.addPitchEvent(0, 1.0f);
		channel.addVolumeEvent(0, 0.75f);
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
	}
	
	@Test
	public void shouldAddAllEventsAddedToSameTick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		channel.addInstrumentEvent(0, "PIANO1");
		channel.addPitchEvent(0, 1.0f);
		channel.addVolumeEvent(0, 0.75f);
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
	}
	
	@Test
	public void shouldOnlyReturnNoteEventIfNoteStartsAtSpecifiedTick() {
		channel.addNoteEvent(0, new NoteEvent(100, 10)); // Spans ticks 0 to 9
		
		ChannelEvents channelEventsAtTick1 = channel.getEventsAtTick(1);
		
		assertNull(channelEventsAtTick1.getNote());
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

		Set<TickChannelEvents> allEventsSet = channel.getEvents();

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
		return new TickChannelEvents(tick, new ChannelEvents(noteEvent, instrument, volume, pitch));
	}
}


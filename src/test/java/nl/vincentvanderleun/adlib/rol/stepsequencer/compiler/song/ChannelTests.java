package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.InstrumentEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.NoteEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.PitchMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event.VolumeMultiplierEvent;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.NoteValue;

public class ChannelTests {
	private static final int CHANNEL = 0;
	private static final NoteValue SOME_NOTE = NoteValue.B;
	private static final NoteValue OTHER_NOTE = NoteValue.C;
	private static final int SOME_OCTAVE = 4;
	private static final int NO_TRANSPOSE = 0;
	private static NoteEvent SOME_NOTE_EVENT = new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE);
	private static NoteEvent OTHER_NOTE_EVENT = new NoteEvent(OTHER_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE);
	
	private final Channel channel = new Channel(CHANNEL);

	@Test
	public void shouldReturnEmptyEventWhenThereAreNoEventsAddedToTheTick() {
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertNull(channelEvents.getNote());
		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getPitch());
		assertNull(channelEvents.getVolume());
	}

	@Test
	public void shouldAddNonNoteEventsToATick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
		assertNull(channelEvents.getInstrument());
		assertNull(channelEvents.getPitch());
		assertNull(channelEvents.getVolume());
	}

	@Test
	public void shouldAddOtherEventsToATick() {
		channel.addInstrumentEvent(0, new InstrumentEvent("PIANO1"));
		channel.addPitchEvent(0, new PitchMultiplierEvent(1.0f));
		channel.addVolumeEvent(0, new VolumeMultiplierEvent(0.75f));
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
		assertNull(channelEvents.getNote());
	}
	
	@Test
	public void shouldAddAllEventsAddedToSameTick() {
		channel.addNoteEvent(0, SOME_NOTE_EVENT);
		channel.addInstrumentEvent(0, new InstrumentEvent("PIANO1"));
		channel.addPitchEvent(0, new PitchMultiplierEvent(1.0f));
		channel.addVolumeEvent(0, new VolumeMultiplierEvent(0.75f));
		
		ChannelEvents channelEvents = channel.getEventsAtTick(0);
		
		assertEquals(0, channelEvents.getTick());
		assertEquals(SOME_NOTE_EVENT, channelEvents.getNote());
		assertEquals("PIANO1", channelEvents.getInstrument());
		assertEquals(1.0f, channelEvents.getPitch());
		assertEquals(0.75f, channelEvents.getVolume());
	}
	
	@Test
	public void shouldOnlyReturnNoteEventIfNoteStartsAtSpecifiedTick() {
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 10, SOME_OCTAVE, NO_TRANSPOSE)); // 0..9
		
		ChannelEvents channelEventsAtTick1 = channel.getEventsAtTick(1);
		
		assertEquals(1, channelEventsAtTick1.getTick());
		assertNull(channelEventsAtTick1.getNote());
	}

	@Test
	public void shouldAddNonOverlappingNotes() {
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE)); // Spans tick 0
		channel.addNoteEvent(1, new NoteEvent(SOME_NOTE, 2, SOME_OCTAVE, NO_TRANSPOSE)); // Spans tick 1 and 2
		channel.addNoteEvent(3, new NoteEvent(SOME_NOTE, 3, SOME_OCTAVE, NO_TRANSPOSE)); // Spans tick 3, 4 and 5
		
		ChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		ChannelEvents channelEventsAtTick1 = channel.getEventsAtTick(1);
		ChannelEvents channelEventsAtTick3 = channel.getEventsAtTick(3);
		
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
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 3, SOME_OCTAVE, NO_TRANSPOSE)); // Spans tick 0, 1, 2
		channel.addNoteEvent(2, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));

		ChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		ChannelEvents channelEventsAtTick2 = channel.getEventsAtTick(2);

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
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		channel.addNoteEvent(1, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		channel.addNoteEvent(2, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		channel.addNoteEvent(3, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		
		channel.addNoteEvent(0, new NoteEvent(SOME_NOTE, 3, SOME_OCTAVE, NO_TRANSPOSE)); // Spans ticks 0, 1 and 2

		ChannelEvents channelEventsAtTick0 = channel.getEventsAtTick(0);
		ChannelEvents channelEventsAtTick3 = channel.getEventsAtTick(3);

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
			channel.addNoteEvent(30, new NoteEvent(SOME_NOTE, 0, SOME_OCTAVE, NO_TRANSPOSE));
		});
	}

	@Test
	public void shouldThrowWhenAddingNotesOnTickSmallerThanZero() {
		assertThrows(IllegalArgumentException.class, () -> {
			channel.addNoteEvent(-1, new NoteEvent(SOME_NOTE, 10, SOME_OCTAVE, NO_TRANSPOSE));
		});
	}
	
	@Test
	public void shouldListAllAddedEventsInSortedOrder() {
		// Tick 1
		channel.addNoteEvent(1, new NoteEvent(SOME_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		// Tick 0
		channel.addInstrumentEvent(0, new InstrumentEvent("PIANO1"));
		// Tick 3
		channel.addNoteEvent(3, new NoteEvent(OTHER_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		channel.addInstrumentEvent(3, new InstrumentEvent("PIANO2"));
		channel.addVolumeEvent(3, new VolumeMultiplierEvent(0.75f));
		channel.addPitchEvent(3, new PitchMultiplierEvent(2.0f));
		// Tick 2
		channel.addNoteEvent(2, new NoteEvent(OTHER_NOTE, 1, SOME_OCTAVE, NO_TRANSPOSE));
		channel.addVolumeEvent(2, new VolumeMultiplierEvent(1.0f));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(4, allEventsSet.size());
		
		var allEvents = new ArrayList<>(allEventsSet);
		
		assertEquals(ChannelEvents.fromInstrumentOnly(CHANNEL, 0, "PIANO1"), allEvents.get(0));
		assertEquals(ChannelEvents.fromNoteEvenOnly(CHANNEL, 1, SOME_NOTE_EVENT), allEvents.get(1));
		assertEquals(createNoteAndVolumeEvent(2, OTHER_NOTE_EVENT, 1.0f), allEvents.get(2));
		assertEquals(ChannelEvents.fromAllEvents(CHANNEL, 3, OTHER_NOTE_EVENT, "PIANO2", 0.75f, 2.0f), allEvents.get(3));
	}
	
	@Test
	public void shouldNormalizeInstrumentEventsWhenAddingTwoDuplicateOnesAfterEachOther() {
		channel.addInstrumentEvent(10, new InstrumentEvent("PIANO1"));
		channel.addInstrumentEvent(30, new InstrumentEvent("PIANO1"));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromInstrumentOnly(CHANNEL, 10, "PIANO1"), allEventsSet.iterator().next());
	}

	@Test
	public void shouldNormalizeInstrumentEventsWhenAddingDuplicateEventBeforeOther() {
		channel.addInstrumentEvent(30, new InstrumentEvent("PIANO1"));
		channel.addInstrumentEvent(10, new InstrumentEvent("PIANO1"));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromInstrumentOnly(CHANNEL, 10, "PIANO1"), allEventsSet.iterator().next());
	}

	@Test
	public void shouldNormalizeVolumeEventsWhenAddingTwoDuplicateOnesAfterEachOther() {
		channel.addVolumeEvent(10, new VolumeMultiplierEvent(1.23f));
		channel.addVolumeEvent(30, new VolumeMultiplierEvent(1.23f));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromVolumeOnly(CHANNEL, 10, 1.23f), allEventsSet.iterator().next());
	}

	@Test
	public void shouldNormalizeVolumeEventsWhenAddingDuplicateEventBeforeOther() {
		channel.addVolumeEvent(30, new VolumeMultiplierEvent(1.23f));
		channel.addVolumeEvent(10, new VolumeMultiplierEvent(1.23f));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromVolumeOnly(CHANNEL, 10, 1.23f), allEventsSet.iterator().next());
	}

	@Test
	public void shouldNormalizePitchEventsWhenAddingTwoDuplicateOnesAfterEachOther() {
		channel.addPitchEvent(10, new PitchMultiplierEvent(13.37f));
		channel.addPitchEvent(30, new PitchMultiplierEvent(13.37f));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromPitchOnly(CHANNEL, 10, 13.37f), allEventsSet.iterator().next());
	}

	@Test
	public void shouldNormalizePitchEventsWhenAddingDuplicateEventBeforeOther() {
		channel.addPitchEvent(30, new PitchMultiplierEvent(13.37f));
		channel.addPitchEvent(10, new PitchMultiplierEvent(13.37f));

		Set<ChannelEvents> allEventsSet = channel.getAllEvents();

		assertEquals(1, allEventsSet.size());
		
		assertEquals(ChannelEvents.fromPitchOnly(CHANNEL, 10, 13.37f), allEventsSet.iterator().next());
	}

	private ChannelEvents createNoteAndVolumeEvent(int tick, NoteEvent noteEvent, Float volume) {
		return ChannelEvents.fromAllEvents(CHANNEL, tick, noteEvent, null, volume, null);
	}
}


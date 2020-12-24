package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NormalizedEventsTests {
	private NormalizedEvents<Integer> normalizedEvents = new NormalizedEvents<>();
	
	@Test
	public void shouldAddEvent() {
		normalizedEvents.add(0, 1337);
		
		int actual = normalizedEvents.get(0);

		assertEquals(1337, actual);

		assertEquals(1, normalizedEvents.getMap().size());
	}

	@Test
	public void shouldAddNonDuplicateEvents() {
		normalizedEvents.add(20, 42);
		normalizedEvents.add(10, 1337);
		
		int actualAtTick10 = normalizedEvents.get(10);
		int actualAtTick20 = normalizedEvents.get(20);

		assertEquals(1337, actualAtTick10);
		assertEquals(42, actualAtTick20);

		assertEquals(2, normalizedEvents.getMap().size());
	}

	@Test
	public void shouldAddEventsWhenDuplicateEventsDoNotFollowUp() {
		normalizedEvents.add(0, 10);
		normalizedEvents.add(1, 20);
		normalizedEvents.add(2, 10);
		
		int actualAtTick0 = normalizedEvents.get(0);
		int actualAtTick1 = normalizedEvents.get(1);
		int actualAtTick2 = normalizedEvents.get(2);

		assertEquals(10, actualAtTick0);
		assertEquals(20, actualAtTick1);
		assertEquals(10, actualAtTick2);
		
		assertEquals(3, normalizedEvents.getMap().size());
	}
	
	@Test
	public void shouldNotAddEventIfPrecedingEventIsTheSame() {
		normalizedEvents.add(10, 42);
		normalizedEvents.add(20, 42);	// Not added, because same event precedes it
		normalizedEvents.add(30, 1337);
		
		int actualAtTick10 = normalizedEvents.get(10);
		Integer actualAtTick20 = normalizedEvents.get(20);
		int actualAtTick30 = normalizedEvents.get(30);

		assertEquals(42, actualAtTick10);
		assertNull(actualAtTick20);
		assertEquals(1337, actualAtTick30);
		
		assertEquals(2, normalizedEvents.getMap().size());
	}
	
	@Test
	public void shouldDeleteDuplicateEventThatDirectlySucceedAddedOne() {
		normalizedEvents.add(10, 42);
		normalizedEvents.add(30, 1337);	// This event will be deleted
		normalizedEvents.add(20, 1337);

		int actualAtTick10 = normalizedEvents.get(10);
		int actualAtTick20 = normalizedEvents.get(20);
		Integer actualAtTick30 = normalizedEvents.get(30);

		assertEquals(42, actualAtTick10);
		assertEquals(1337, actualAtTick20);
		assertNull(actualAtTick30);
		
		assertEquals(2, normalizedEvents.getMap().size());
	}
	
	@Test
	public void shouldThrowWhenAddingNullEvent() {
		assertThrows(NullPointerException.class, () -> {
			normalizedEvents.add(0, null);
		});
	}
}

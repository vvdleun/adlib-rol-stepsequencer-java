package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NormalizedEventsTests {
	private NormalizedEventMap<Integer> normalizedEventMap = new NormalizedEventMap<>();
	
	@Test
	public void shouldAddEvent() {
		normalizedEventMap.add(0, 1337);
		
		int actual = normalizedEventMap.get(0);

		assertEquals(1337, actual);

		assertEquals(1, normalizedEventMap.getMap().size());
	}

	@Test
	public void shouldAddNonDuplicateEvents() {
		normalizedEventMap.add(20, 42);
		normalizedEventMap.add(10, 1337);
		
		int actualAtTick10 = normalizedEventMap.get(10);
		int actualAtTick20 = normalizedEventMap.get(20);

		assertEquals(1337, actualAtTick10);
		assertEquals(42, actualAtTick20);

		assertEquals(2, normalizedEventMap.getMap().size());
	}

	@Test
	public void shouldAddEventsWhenDuplicateEventsDoNotFollowUp() {
		normalizedEventMap.add(0, 10);
		normalizedEventMap.add(1, 20);
		normalizedEventMap.add(2, 10);
		
		int actualAtTick0 = normalizedEventMap.get(0);
		int actualAtTick1 = normalizedEventMap.get(1);
		int actualAtTick2 = normalizedEventMap.get(2);

		assertEquals(10, actualAtTick0);
		assertEquals(20, actualAtTick1);
		assertEquals(10, actualAtTick2);
		
		assertEquals(3, normalizedEventMap.getMap().size());
	}
	
	@Test
	public void shouldNotAddEventIfPrecedingEventIsTheSame() {
		normalizedEventMap.add(10, 42);
		normalizedEventMap.add(20, 42);	// Not added, because same event precedes it
		normalizedEventMap.add(30, 1337);
		
		int actualAtTick10 = normalizedEventMap.get(10);
		Integer actualAtTick20 = normalizedEventMap.get(20);
		int actualAtTick30 = normalizedEventMap.get(30);

		assertEquals(42, actualAtTick10);
		assertNull(actualAtTick20);
		assertEquals(1337, actualAtTick30);
		
		assertEquals(2, normalizedEventMap.getMap().size());
	}
	
	@Test
	public void shouldDeleteDuplicateEventThatDirectlySucceedAddedOne() {
		normalizedEventMap.add(10, 42);
		normalizedEventMap.add(30, 1337);	// This event will be deleted
		normalizedEventMap.add(20, 1337);

		int actualAtTick10 = normalizedEventMap.get(10);
		int actualAtTick20 = normalizedEventMap.get(20);
		Integer actualAtTick30 = normalizedEventMap.get(30);

		assertEquals(42, actualAtTick10);
		assertEquals(1337, actualAtTick20);
		assertNull(actualAtTick30);
		
		assertEquals(2, normalizedEventMap.getMap().size());
	}
	
	@Test
	public void shouldThrowWhenAddingNullEvent() {
		assertThrows(NullPointerException.class, () -> {
			normalizedEventMap.add(0, null);
		});
	}
}

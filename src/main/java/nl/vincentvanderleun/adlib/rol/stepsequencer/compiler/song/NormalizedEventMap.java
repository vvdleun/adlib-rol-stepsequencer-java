package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class NormalizedEventMap<T extends Comparable<T>> {
	private final NavigableMap<Integer, T> events;
	
	public NormalizedEventMap() {
		this.events = new TreeMap<>();
	}
	
	public void add(int tick, T event) {
		if(event == null) {
			throw new NullPointerException("Event is not allowed to be null");
		}
		
		// If exactly the same event preceded the specified one, don't add this one
		Entry<Integer, T> earlierEvent = events.lowerEntry(tick);
		if(earlierEvent != null && isSameEvent(event, earlierEvent.getValue())) {
			return;
		}

		events.put(tick, event);

		// If exactly the same event succeed the new event, delete that one
		Entry<Integer, T> laterEvent = events.higherEntry(tick);
		if(laterEvent != null && isSameEvent(event, laterEvent.getValue())) {
			events.remove(laterEvent.getKey());
		}
	}
	
	public T get(int tick) {
		return events.get(tick);
	}
	
	private boolean isSameEvent(T addedEvent, T otherEvent) {
		return addedEvent.compareTo(otherEvent) == 0;	
	}
	
	public SortedMap<Integer, T> getMap() {
		return events;
	}
}

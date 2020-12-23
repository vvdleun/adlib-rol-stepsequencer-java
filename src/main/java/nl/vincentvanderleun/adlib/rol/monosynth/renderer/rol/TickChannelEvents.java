package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.util.Objects;

public class TickChannelEvents implements Comparable<TickChannelEvents> {
	private final int tick;
	private final ChannelEvents channelEvents;
	
	TickChannelEvents(int tick, ChannelEvents channelEvents) {
		this.tick = tick;
		this.channelEvents = channelEvents;
	}
	
	public int getTick() {
		return tick;
	}
	
	public ChannelEvents getChannelEvents() {
		return channelEvents;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(channelEvents, tick);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TickChannelEvents other = (TickChannelEvents) obj;
		return Objects.equals(channelEvents, other.channelEvents) && tick == other.tick;
	}

	@Override
	public String toString() {
		return "TickChannelEvents [tick=" + tick + ", channelEvents=" + channelEvents + "]";
	}

	@Override
	public int compareTo(TickChannelEvents other) {
		if(tick > other.getTick()) {
			return 1;
		} else if(tick < other.getTick()) {
			return -1;
		}
		return 0;
	}
}

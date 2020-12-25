package nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.event;

public class InstrumentEvent extends Event implements Comparable<InstrumentEvent> {
	private final String instrument;
	
	public InstrumentEvent(String instrument) {
		super(EventType.INSTRUMENT);
		this.instrument = instrument;
	}
	
	public String getInstrument() {
		return instrument;
	}

	@Override
	public int compareTo(InstrumentEvent o) {
		return instrument.compareTo(o.getInstrument());
	}
}

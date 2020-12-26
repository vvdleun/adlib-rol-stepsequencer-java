package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format;

public class Header {
	private final String signature;
	private final int ticksPerBeat;
	private final int beatsPerMeasure;
	private final int scaleY;
	private final int scaleX;
	private final int songMode;
	private final int[] counters;

	public Header(String signature, int ticksPerBeat, int beatsPerMeasure, int scaleY, int scaleX, int songMode, int[] counters) {
		this.signature = signature;
		this.ticksPerBeat = ticksPerBeat;
		this.beatsPerMeasure = beatsPerMeasure;
		this.scaleY = scaleY;
		this.scaleX = scaleX;
		this.songMode = songMode;
		this.counters = counters;
	}
	
	public String getSignature() {
		return signature;
	}

	public int getTicksPerBeat() {
		return ticksPerBeat;
	}

	public int getBeatsPerMeasure() {
		return beatsPerMeasure;
	}

	public int getScaleY() {
		return scaleY;
	}

	public int getScaleX() {
		return scaleX;
	}

	public int getSongMode() {
		return songMode;
	}

	public int[] getCounters() {
		return counters;
	}
}

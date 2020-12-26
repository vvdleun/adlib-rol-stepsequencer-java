package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Timbre;

public class TimbreTrack extends DefaultEventTrack<Timbre>  {
	public TimbreTrack(String trackName, List<Timbre> timbreEvents) {
		super(trackName, timbreEvents);
	}
}

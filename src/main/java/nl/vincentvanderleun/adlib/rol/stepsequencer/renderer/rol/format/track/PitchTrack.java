package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Pitch;

public class PitchTrack extends DefaultEventTrack<Pitch>  {
	public PitchTrack(String trackName, List<Pitch> pitchEvents) {
		super(trackName, pitchEvents);
	}
}

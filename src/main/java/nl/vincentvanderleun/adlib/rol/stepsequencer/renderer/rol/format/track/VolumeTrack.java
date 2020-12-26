package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.track;

import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.event.Volume;

public class VolumeTrack extends Track<Volume>  {
	public VolumeTrack(String trackName, List<Volume> volumeEvents) {
		super(trackName, volumeEvents);
	}
}

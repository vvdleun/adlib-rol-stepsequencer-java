package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.adlib.rol.stepsequencer.compiler.song.CompiledSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFile;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.AdLibRolFileWriter;
import nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.format.Channel;

public class AdLibRolSongRenderer {

	public void renderRolFile(CompiledSong song, OutputStream outputStream) throws IOException {
		CompiledSongToRolFileConverter compiledSongToRolFileConverter = new CompiledSongToRolFileConverter();
		
		AdLibRolFile rol = compiledSongToRolFileConverter.convertToAdLibRolFile(song);
		
		reportStats(song.getSongMode(), rol);
		
		AdLibRolFileWriter writer = new AdLibRolFileWriter();
		
		writer.writeRolFile(rol, outputStream);
	}
	
	private void reportStats(SongMode songMode, AdLibRolFile rol) {
		long countEvents = rol.getTempoTrack().getEvents().size();
		long countChannels = 0;
		
		for(Channel channel : rol.getChannels()) {
			countEvents += channel.getPitchTrack().getEvents().size()
					+ channel.getTimbreTrack().getEvents().size()
					+ channel.getVoiceTrack().getEvents().size()
					+ channel.getVolumeTrack().getEvents().size();

			if(channel.getVoiceTrack().getTotalTicks() > 0) {
				countChannels++;
			}
		}
		
		System.out.println("Successfully generated ROL file with " + countEvents + " event(s) and using up "
				+ countChannels + " of " + songMode.getChannels() + " available channels.");
	}
}

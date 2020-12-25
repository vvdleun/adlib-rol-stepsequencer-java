package nl.vincentvanderleun.adlib.rol.stepsequencer.renderer.rol.writer;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.dos.io.DosBinaryFileWriter;

public class AdLibRolFileWriter {
	
	public AdLibRolFileWriter() {
	}
	
	public void writeRolFile(AdLibRolFile rol, OutputStream outputStream) throws IOException {
		final DosBinaryFileWriter writer = new DosBinaryFileWriter(outputStream);

		writeHeader(rol, writer);
		writeTempoTrack(rol, writer);
		writeChannels(rol, writer);
	}
	
	private void writeHeader(AdLibRolFile rol, DosBinaryFileWriter writer) throws IOException {
		final AdLibRolFile.Header header = rol.getHeader();
		
		// Version 0.4
		writer.writeInt(AdLibRolFile.VERSION_MAJOR);
		writer.writeInt(AdLibRolFile.VERSION_MINOR);
		// Signature
		writer.writeZeroTerminatedAsciiString(header.getSignature(), 40);
		// Ticks per beat and beats per measure
		writer.writeWord(header.getTicksPerBeat());
		writer.writeWord(header.getBeatsPerMeasure());
		// Visual Composer scale Y and X
		writer.writeWord(header.getScaleY());
		writer.writeWord(header.getScaleX());
		// Reserved
		writer.writeUnsignedByte(0);
		// Song mode
		writer.writeUnsignedByte(header.getSongMode());
		// Counters
		for(int i = 0; i < header.getCounters().length; i++) {
			writer.writeWord(header.getCounters()[i]);
		}
		// Filler
		writer.writeEmptyBytes(38);
	}
	
	private void writeTempoTrack(AdLibRolFile rol, DosBinaryFileWriter writer) throws IOException {
		final AdLibRolFile.TempoTrack track = rol.getTempoTrack();

		// Track name
		writer.writeZeroTerminatedAsciiString(track.getTrackName(), 15);
		// Main tempo
		writer.writeFloat(track.getTempo());
		// Number of events
		writer.writeWord(track.getTempoMultiplierEvents().size());
		
		for (AdLibRolFile.Tempo tempo : track.getTempoMultiplierEvents()) {
			writer.writeWord(tempo.getTick());
			writer.writeFloat(tempo.getTempoMultiplier());
		}
	}

	private void writeChannels(AdLibRolFile rol, DosBinaryFileWriter writer) throws IOException {
		for (AdLibRolFile.Channel channel : rol.getChannels()) {
			writeVoiceTrack(rol, writer, channel);
			writeTimbreTrack(rol, writer, channel);
			writeVolumeTrack(rol, writer, channel);
			writePitchTrack(rol, writer, channel);
		}
	}
	
	private void writeVoiceTrack(AdLibRolFile rol, DosBinaryFileWriter writer, AdLibRolFile.Channel channel) throws IOException {
		final AdLibRolFile.VoiceTrack voiceTrack = channel.getVoiceTrack();
		
		writer.writeZeroTerminatedAsciiString(voiceTrack.getTrackName(), 15);
		writer.writeWord(voiceTrack.getTotalTicks());
		
		for(AdLibRolFile.Note note : voiceTrack.getNotes()) {
			writer.writeWord(note.getNote());
			writer.writeWord(note.getDuration());
		}
	}
	
	private void writeTimbreTrack(AdLibRolFile rol, DosBinaryFileWriter writer, AdLibRolFile.Channel channel) throws IOException {
		final AdLibRolFile.TimbreTrack timbreTrack = channel.getTimbreTrack();

		writer.writeZeroTerminatedAsciiString(timbreTrack.getTrackName(), 15);
		writer.writeWord(timbreTrack.getEvents().size());

		for(AdLibRolFile.Timbre timbre : timbreTrack.getEvents()) {
			writer.writeWord(timbre.getTick());
			writer.writeZeroTerminatedAsciiString(timbre.getTimbre(), 9);
			// Probably the most interesting two bytes in the whole ROL file specification :-)
			writer.writeUnsignedByte(0);
			writer.writeWord(timbre.getUnknown());
		}
	}
	
	private void writeVolumeTrack(AdLibRolFile rol, DosBinaryFileWriter writer, AdLibRolFile.Channel channel) throws IOException {
		final AdLibRolFile.VolumeTrack volumeTrack = channel.getVolumeTrack();

		writer.writeZeroTerminatedAsciiString(volumeTrack.getTrackName(), 15);
		writer.writeWord(volumeTrack.getEvents().size());

		for(AdLibRolFile.Volume volume : volumeTrack.getEvents()) {
			writer.writeWord(volume.getTick());
			writer.writeFloat(volume.getValue());
		}
	}

	private void writePitchTrack(AdLibRolFile rol, DosBinaryFileWriter writer, AdLibRolFile.Channel channel) throws IOException {
		final AdLibRolFile.PitchTrack pitchTrack = channel.getPitchTrack();

		writer.writeZeroTerminatedAsciiString(pitchTrack.getTrackName(), 15);
		writer.writeWord(pitchTrack.getEvents().size());

		for(AdLibRolFile.Pitch pitch : pitchTrack.getEvents()) {
			writer.writeWord(pitch.getTick());
			writer.writeFloat(pitch.getValue());
		}
	}
}

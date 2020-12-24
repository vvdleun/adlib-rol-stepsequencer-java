package nl.vincentvanderleun.adlib.rol.monosynth.renderer.rol;

import java.io.IOException;
import java.io.OutputStream;

import nl.vincentvanderleun.adlib.rol.monosynth.song.Song;
import nl.vincentvanderleun.adlib.rol.monosynth.song.SongMode;
import nl.vincentvanderleun.dos.io.DosBinaryFileWriter;

public class AdLibRolSongRenderer {
	private final DosBinaryFileWriter writer;
	
	public AdLibRolSongRenderer(OutputStream outputStream) {
		writer = new DosBinaryFileWriter(outputStream);
	}
	
	public void renderRolFile(Song song) throws IOException {
		SongToRolEventsConverter songToRolConverter = new SongToRolEventsConverter(song);
		
		songToRolConverter.convertToNormalizedRolEvents();
		
	}
	
	private void writeHeader(Song song) throws IOException {
		// Version 0.4
		writer.writeInt(0);
		writer.writeInt(4);
		// Signature
		writer.writeZeroTerminatedAsciiString("\\roll\\default", 40);
		// Ticks per beat and beats per measure
		writer.writeWord(song.getHeader().getTicksPerBeat());
		writer.writeWord(song.getHeader().getBeatsPerMeasure());
		// Visual Composer scale Y and X
		writer.writeWord(0x30);
		writer.writeWord(0x38);
		// Reserved
		writer.writeUnsignedByte(0);
		// Song mode
		int songMode = song.getHeader().getMode() == SongMode.PERCUSSIVE ? 0 : 1;
		writer.writeUnsignedByte(songMode);
		// Counters (TODO: implement)
		writer.writeEmptyBytes(90);
		// Filler
		writer.writeEmptyBytes(38);
	}
	
	private void writeTempoTrack(Song song) throws IOException {
		// Title track
		writer.writeZeroTerminatedAsciiString("Tempo", 15);
		writer.writeFloat(song.getHeader().getTempo());
		// TODO implement support for tempo track
		writer.writeWord(1);
		writer.writeFloat(1.0f);
	}
	
	private void writeChannelTracks(Song song) throws IOException {
		
	}
}

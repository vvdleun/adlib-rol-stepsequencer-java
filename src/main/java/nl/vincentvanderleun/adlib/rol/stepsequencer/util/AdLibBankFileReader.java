package nl.vincentvanderleun.adlib.rol.stepsequencer.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.vincentvanderleun.dos.io.DosBinaryFileReader;

public class AdLibBankFileReader {
	private final DosBinaryFileReader reader;
	
	private int readHeaderBytes;
	private int instrumentsUsed;
	private int instrumentsTotal;
	private long offsetNames;

	private static SortedSet<String> readInstrumentNamesFrom(String path) throws IOException {
		try(var inputStream = new BufferedInputStream(new FileInputStream(path))) {
			var bnkFileReader = new AdLibBankFileReader(inputStream);
	
			return bnkFileReader.readUsedInstrumentNames();
		}
	}

	private AdLibBankFileReader(InputStream inputStream) {
		this.reader = new DosBinaryFileReader(inputStream);
	}
	
	public SortedSet<String> readUsedInstrumentNames() throws IOException {
		readAndValidateHeader();
		
		reader.skipBytes(calcOffsetStartInstruments());

		var instrumentNames = new TreeSet<String>();
		for(int i = 0; i < instrumentsTotal; i++) {
			Optional<String> instrument = readNextInstrumentName();
			if(instrument.isPresent()) {
				instrumentNames.add(instrument.get());
			}
		}

		if(instrumentsUsed != instrumentNames.size()) {
			System.out.println("\nWarning: only " + instrumentNames.size() + " read, while there should have been " + instrumentsUsed + " instruments in the bank file\n");
		}

		return instrumentNames;
	}
	
	private Optional<String> readNextInstrumentName() throws IOException {
		reader.skipBytes(2); // Not interested in data offset
		boolean isUsed = reader.readBoolean();
		if(!isUsed) {
			return Optional.empty();
		}
		String instrumentName = reader.readZeroTerminatedAsciiString(9);
		return Optional.of(instrumentName);
	}
	
	private long calcOffsetStartInstruments() {
		// Work around fact that inputStream does not have "seek()"
		return offsetNames - readHeaderBytes;
	}
	
	private void readAndValidateHeader() throws IOException {
		this.readHeaderBytes = 0;
		
		int majorVersion = reader.readUnsignedByte();
		this.readHeaderBytes++;
		
		int minorVersion = reader.readUnsignedByte();
		this.readHeaderBytes++;

		String signature = reader.readAsciiString(6);
		this.readHeaderBytes += 6;
	
		this.instrumentsUsed = reader.readWord();
		this.readHeaderBytes += 2;

		this.instrumentsTotal = reader.readWord();
		this.readHeaderBytes += 2;

		this.offsetNames = reader.readDoubleWord();
		this.readHeaderBytes += 4;
		
		String version = majorVersion + "." + minorVersion;
		if(!version.equals("1.0") || !signature.equals("ADLIB-")) {
			throw new IOException("The file does not appear to be a valid Ad Lib Instrument Bank (BNK) file.");
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(readInstrumentNamesFrom("C:\\DOS\\TEMP\\TESTBNK.BNK"));
	}
}

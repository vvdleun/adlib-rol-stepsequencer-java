package nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl;

import java.io.BufferedReader;
import java.io.IOException;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.ParseException;

public class LineParser {
	private final BufferedReader reader;
	private long lineNumber = 0;
	private boolean endOfFileReached = false;
	
	public LineParser(BufferedReader reader) {
		this.reader = reader;
	}
	
	public String parseLine() throws IOException {
		String line = parseLineOrEOF();
		if(endOfFileReached) {
			throw new ParseException("Unexpected end of file at line " + lineNumber);
		}
		return line;
	}

	public String parseLineOrEOF() throws IOException {
		String line = reader.readLine();

		// Handle internal state
		lineNumber++;
		if(line == null) {
			endOfFileReached = true;
			return null;
		}
		
		return line.trim();
	}
	
	public long getLineNumber() {
		return lineNumber;
	}
	
	public boolean isEndOfFileReached() {
		return endOfFileReached;
	}
}

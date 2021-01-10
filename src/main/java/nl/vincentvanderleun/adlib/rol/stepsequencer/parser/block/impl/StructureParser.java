package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import java.io.IOException;
import java.util.regex.Pattern;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class StructureParser {
	private static final String EXPECTED_START_LIST = "Expected line with \"- key value\" to start a list at line ";

	private final LineParser lineParser;

	public StructureParser(LineParser lineParser) {
		this.lineParser = lineParser;
	}

	public void readContentOfBlock(BlockLineConsumer blockLineConsumer) throws IOException {
		String line;
		while(!endOfBlock(line = lineParser.parseLineOrEOF())) {
			blockLineConsumer.accept(new BlockLine(line, lineParser));
		}
	}

	private boolean endOfBlock(String line) throws ParseException {
		boolean result = line == null || line.trim().equals("");

		if(line != null && isStartHeaderBlock(line)) {
			throw new ParseException("An empty line is required to end a block at line " + lineParser.getLineNumber());
		}

		return result;
	}

	public String parseStartHeaderBlockWithoutValue() throws IOException {
		return parseStartHeaderBlock()[0];
	}

	public String[] parseStartHeaderBlock() throws IOException {
		String line = lineParser.parseLine();

		if(!isStartHeaderBlock(line)) {
			throw new ParseException("Expected line with \"[header]\" at line " + lineParser.getLineNumber());
		}

		String fullHeader = line.substring(1, line.length() - 1).trim();		
		String[] headerAndValue = fullHeader.split(Pattern.quote(" "), 2);

		if(headerAndValue.length == 1) {
			String temp = headerAndValue[0];
			headerAndValue = new String[2];
			headerAndValue[0] = temp;
			headerAndValue[1] = "";
		}

		return headerAndValue;
	}

	private boolean isStartHeaderBlock(String line) {
		return line.startsWith("[") && line.endsWith("]");
	}

	public String[] parseKeyValue() throws IOException {
		return parseKeyValue(lineParser.parseLine());
	}

	public String[] parseBlockKeyValue(String line) throws ParseException {
		// TODO Inside a block the line is already parsed :-/
		return parseKeyValue(line);
	}

	private String[] parseKeyValue(String line) throws ParseException {
		String[] keyValue = line.split("=", 2);
		if(keyValue.length != 2) {
			throw new ParseException("Expected line with \"key=value\" at line " + lineParser.getLineNumber());
		}

		keyValue[0] = keyValue[0].trim();
		keyValue[1] = keyValue[1].trim();

		return keyValue;
	}

	public boolean isKeyValue(String line) {
		String[] keyValue = line.split("=", 2);

		return keyValue.length == 2;
	}

	public String[] parseStartList(String line) throws ParseException {
		if(!isStartList(line)) {
			throw new ParseException(EXPECTED_START_LIST + lineParser.getLineNumber());
		}
		line = line.substring(1).trim();
		String[] listKeyAndValue = line.split(Pattern.quote(" "), 2);

		listKeyAndValue[0] = listKeyAndValue[0].trim();
		listKeyAndValue[1] = listKeyAndValue[1].trim();

		if(listKeyAndValue.length != 2) {
			throw new ParseException(EXPECTED_START_LIST + lineParser.getLineNumber());
		}
		return listKeyAndValue;
	}

	public boolean isStartList(String line) {
		return line.startsWith("-");
	}
}

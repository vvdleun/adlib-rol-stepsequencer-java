package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class BlockLine {
	private final String line;
	private final LineParser lineParser;
	private final StructureParser structureParser;
	private final ValueParser valueParser;
	
	public BlockLine(String line, LineParser lineParser) {
		this.line = line;
		this.lineParser = lineParser;
		this.structureParser = new StructureParser(lineParser);
		this.valueParser = new ValueParser();
	}
	
	public String getRawLine() {
		return line;
	}
	
	public boolean reachedEndOfFile() {
		return line == null;
	}
	
	public boolean isKeyValue() {
		return structureParser.isKeyValue(line);
	}
	
	public String parseKey() throws ParseException {
		return parseKeyValue()[0];
	}

	public String parseValue() throws ParseException {
		return parseKeyValue()[1];
	}

	private String[] parseKeyValue() throws ParseException {
		return structureParser.parseBlockKeyValue(line);
	}

	public int parseValueAsInteger() throws ParseException {
		return valueParser.parseInteger(parseValue(), lineParser.getLineNumber());
	}
	
	public float parseValueAsFloat() throws ParseException {
		return valueParser.parseFloat(parseValue(), lineParser.getLineNumber());
	}

	public boolean parseValueAsBoolean() throws ParseException {
		return valueParser.parseBoolean(parseValue(), lineParser.getLineNumber());
	}
	
	public boolean isStartOfList() {
		return structureParser.isStartList(line);
	}
	
	public String parseStartOfListKey() throws ParseException {
		return structureParser.parseStartList(line)[0];
	}
	
	public String parseStartOfListValue() throws ParseException {
		return structureParser.parseStartList(line)[1];
	}
}

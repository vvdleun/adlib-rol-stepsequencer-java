package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;

public class SongHeaderBlockParser extends BlockParser<SongHeader> {
	private static final String MISSING_SONG_HEADER_TEXT = "The file must start with a \"[SONG]\" block with \"version=1\" key/value";
	private static final String UNSUPPORTED_VERSION_TEXT = "This program is not compatible with the version declared at line ";

	public SongHeaderBlockParser(LineParser lineParser, Supplier<SongHeader> defaultSongHeaderSupplier) {
		super(lineParser, defaultSongHeaderSupplier);
	}
	
	@Override
	public SongHeader parse() throws IOException {
		validateFileHeader();

		final SongHeader songHeader = defaultValueSupplier.get();
		final Set<String> parsedKeys = new HashSet<>();
		
		structureParser.readContentOfBlock((line) -> {
			// The [SONG] block only supports key/values
			final String key = line.parseKey();
			final String value = line.parseValue();					
			
			if(!parsedKeys.add(key)) {
				throw new ParseException("Encountered key \"" + key + "\" multiple times in HEADER block at line " + lineParser.getLineNumber());
			}
			
			switch(key) {
				case "target":
					if(value.equals("RL2")) {
						throw new ParseException("<easter-egg>You know, producing Ad Lib Gold RL2 files is on my bucket list... but since I have "
								+ "exactly zero means to playback those songs, I have been unsuccesfull in my attempts to reverse-engineer those files :(</easter-egg>");
					} else if(!value.equals("ROL")) {
						throw new ParseException("This program does not support the declared target \"" + value + "\" at line " + lineParser.getLineNumber());
					}
					break;
				case "tempo":
					final float tempo = line.parseValueAsFloat();
					songHeader.setTempo(tempo);
					break;
				case "ticksPerBeat":
					final int ticksPerBeat = line.parseValueAsInteger();
					songHeader.setTicksPerBeat(ticksPerBeat);
					break;
				case "beatsPerMeasure":
					final int beatsPerMeasure = line.parseValueAsInteger();
					songHeader.setBeatsPerMeasure(beatsPerMeasure);
					break;
				case "mode":
					final SongMode songMode = parseSongMode(value);
					songHeader.setMode(songMode);
					break;
				default:
					throw new ParseException("Key '" + key + "' is not supported inside the [SONG] block at line " + lineParser.getLineNumber());
			}
		});
		
		return songHeader;
	}
	
	private SongMode parseSongMode(String value) throws ParseException {
		switch(value) {
			case "MELODIC":
				return SongMode.MELODIC;
			case "PERCUSSIVE":
				return SongMode.PERCUSSIVE;
			default:
				throw new ParseException("ParsedSong mode '" + value + "' is not supported inside the [SONG] block at line " + lineParser.getLineNumber());
		}
	}
	
	private void validateFileHeader() throws IOException {
		// Give a very specific, verbose, error message when something's wrong with the header
		String firstHeader;
		String[] version;
		try {
			firstHeader = structureParser.parseStartHeaderBlockWithoutValue();
			version = structureParser.parseKeyValue();
		} catch(ParseException ex) {
			throw new ParseException(MISSING_SONG_HEADER_TEXT, ex);
		}
		
		if(!firstHeader.equals("SONG")) {
			throw new ParseException(MISSING_SONG_HEADER_TEXT);
		}
		if(!version[0].equals("version")) {
			throw new ParseException(MISSING_SONG_HEADER_TEXT);
		}
		if(!version[1].equals("1") && !version[1].equals("0")) {
			throw new ParseException(UNSUPPORTED_VERSION_TEXT + lineParser.getLineNumber());
		}
	}
}

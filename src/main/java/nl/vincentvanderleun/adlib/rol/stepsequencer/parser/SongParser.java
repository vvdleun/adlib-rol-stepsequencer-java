package nl.vincentvanderleun.adlib.rol.stepsequencer.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Patch;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.SongMode;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Target;
import nl.vincentvanderleun.adlib.rol.stepsequencer.model.Voice;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.PatchBlockParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.PatternBlockParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.TrackBlockParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.SongHeaderBlockParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.block.impl.StructureParser;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.ParsedSong;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.SongHeader;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.song.track.Track;

public class SongParser {
	private final LineParser lineParser;
	private final StructureParser structureParser;

	private SongHeader header;
	private final List<Patch> patches = new ArrayList<>();
	private final List<Pattern> patterns = new ArrayList<>();
	private Track track;
	
	public static final ParsedSong parse(String path) throws IOException {
		try(var reader = new BufferedReader(new FileReader(path))) {
			var parser = new SongParser(reader);
			return parser.parse();
		}
	}

	private SongParser(BufferedReader reader) {
		this.lineParser = new LineParser(reader);
		this.structureParser = new StructureParser(lineParser);
	}

	private ParsedSong parse() throws IOException {
		parseSongHeader();
		
		while(!lineParser.isEndOfFileReached()) {
			String[] nextHeader = structureParser.parseStartHeaderBlock();
			
			switch(nextHeader[0]) {
				case "PATCH":
					parseNextPatch(nextHeader[1]);
					break;
				case "PATTERN":
					parseNextPattern(nextHeader[1]);
					break;
				case "TRACK":
					parseTrack(header);
					break;
				case "SONG":
					throw new ParseException("There can only be one \"SONG\" block in the file at line " + lineParser.getLineNumber());
				default:
					throw new ParseException("Encountered unknown \"" + nextHeader[0] + "\" block at line " + lineParser.getLineNumber());
			}
		}
		
		ParsedSong parsedSong = new ParsedSong();
		parsedSong.setHeader(header);
		parsedSong.setPatches(patches);
		parsedSong.setPatterns(patterns);
		parsedSong.setTrack(track);

		return parsedSong;
	}
	
	private void parseSongHeader() throws IOException {
		SongHeaderBlockParser songHeaderParser = new SongHeaderBlockParser(lineParser, () -> {
			SongHeader defaultSongHeader = new SongHeader();

			defaultSongHeader.setTicksPerBeat(4);
			defaultSongHeader.setBeatsPerMeasure(4);
			defaultSongHeader.setMode(SongMode.MELODIC);
			defaultSongHeader.setTarget(Target.ADLIB_ROL);
			defaultSongHeader.setTempo(120.0F);

			return defaultSongHeader;
		});

		this.header = songHeaderParser.parse();
	}

	private void parseNextPatch(String patchName) throws IOException {
		// Ensure patchName is unique
		boolean patchNameConflict = patches.stream()
				.map(existingPatch -> existingPatch.getName())
				.anyMatch(existingPatchName -> patchName.equals(existingPatchName));

		if (patchNameConflict) {
			throw new ParseException("Patch name \"" + patchName + "\" is not unique at line " + lineParser.getLineNumber());
		}

		PatchBlockParser patchParser = new PatchBlockParser(
				patchName,
				lineParser,
				() -> new Patch(),
				() -> {
					Voice voice = new Voice();
					// Channel is initialized by the PatchParser
					voice.setInstrument("PIANO1");
					voice.setPitch(1.0f);
					voice.setTranspose(0);
					voice.setVolume(0.75f);
					return voice;
				});

		Patch patch = patchParser.parse();

		this.patches.add(patch);
	}

	private void parseNextPattern(String patternName) throws IOException {
		// Ensure patchName is unique
		boolean patternNameConflict = patterns.stream()
				.map(existingPattern -> existingPattern.getName())
				.anyMatch(existingPatternName -> patternName.equals(existingPatternName));

		if (patternNameConflict) {
			throw new ParseException("Pattern name \"" + patternName + "\" is not unique at line " + lineParser.getLineNumber());
		}

		PatternBlockParser patternParser = new PatternBlockParser(
				patternName,
				lineParser,
				() -> new Pattern());

		Pattern pattern = patternParser.parse();

		patterns.add(pattern);
	}

	private void parseTrack(SongHeader header) throws IOException {
		if(track != null) {
			throw new ParseException("Found more than one [TRACK] block at line " + lineParser.getLineNumber());
		}
		
		TrackBlockParser parser = new TrackBlockParser(
				header,
				lineParser,
				() -> new Track());
		
		Track track = parser.parse();
		
		this.track = track;		
	}
}

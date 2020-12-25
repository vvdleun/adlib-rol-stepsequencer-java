package nl.vincentvanderleun.adlib.rol.monosynth.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.PatchBlockParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.PatternBlockParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.SequencerBlockParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.SongHeaderBlockParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl.StructureParser;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.Patch;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.Song;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.SongHeader;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.SongMode;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.Target;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.Voice;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.pattern.Pattern;
import nl.vincentvanderleun.adlib.rol.monosynth.song.parsed.sequencer.Sequencer;

public class MonoSynthInputFileParser {
	private final LineParser lineParser;
	private final StructureParser structureParser;

	private SongHeader header;
	private final List<Patch> patches = new ArrayList<>();
	private final List<Pattern> patterns = new ArrayList<>();
	private Sequencer sequencer;
	
	public static final Song parse(String path) throws IOException {
		try(var reader = new BufferedReader(new FileReader(path))) {
			var parser = new MonoSynthInputFileParser(reader);
			return parser.parse();
		}
	}

	private MonoSynthInputFileParser(BufferedReader reader) {
		this.lineParser = new LineParser(reader);
		this.structureParser = new StructureParser(lineParser);
	}

	private Song parse() throws IOException {
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
				case "SEQUENCER":
					parseSequencer();
					break;
				case "SONG":
					throw new ParseException("There can only be one \"SONG\" block in the file at line " + lineParser.getLineNumber());
				default:
					throw new ParseException("Encountered unsupported \"" + nextHeader[0] + "\" block at line " + lineParser.getLineNumber());
			}
		}
		
		Song song = new Song();
		song.setHeader(header);
		song.setPatches(patches);
		song.setPatterns(patterns);
		song.setSequencer(sequencer);
		
		return song;
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

	private void parseSequencer() throws IOException {
		if(sequencer != null) {
			throw new ParseException("Found more than one [SEQUENCER] block at line " + lineParser.getLineNumber());
		}
		
		SequencerBlockParser parser = new SequencerBlockParser(
				lineParser,
				() -> new Sequencer());
		
		Sequencer sequencer = parser.parse();
		
		this.sequencer = sequencer;		
	}
}

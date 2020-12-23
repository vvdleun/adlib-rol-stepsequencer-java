package nl.vincentvanderleun.adlib.rol.monosynth.parser.block;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import nl.vincentvanderleun.adlib.rol.monosynth.parser.ParseException;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl.BlockLine;
import nl.vincentvanderleun.adlib.rol.monosynth.parser.block.impl.LineParser;
import nl.vincentvanderleun.adlib.rol.monosynth.song.Patch;
import nl.vincentvanderleun.adlib.rol.monosynth.song.Voice;

public class PatchBlockParser extends BlockParser<Patch> {
	private final String patchName;
	private final Supplier<Voice> defaultVoiceSupplier;
	
	public PatchBlockParser(String patchName, LineParser lineParser, Supplier<Patch> defaultPatchSupplier, Supplier<Voice> defaultVoiceSupplier) {
		super(lineParser, defaultPatchSupplier);
		this.patchName = patchName;
		this.defaultVoiceSupplier = defaultVoiceSupplier;
	}
	
	@Override
	public Patch parse() throws IOException {
		if(patchName == null || patchName.isEmpty()) {
			throw new ParseException("No name specified in patch at line " + lineParser.getLineNumber());
		}

		final List<Voice> voices = new ArrayList<>();
		final Set<String> parsedVoiceNames = new HashSet<>();
		final Set<String> parsedVoiceKeys = new HashSet<>();

		structureParser.readContentOfBlock((line) -> {
			// First item must be a start of list
			if(voices.isEmpty() || line.isStartOfList()) {
				// Initialize next voice
				final String voiceName = parseStartOfVoice(line);
				if(!parsedVoiceNames.add(voiceName)) {
					throw new ParseException("Voice \"" + voiceName + "\" is not unique in patch \"" + patchName + "\" at line " + lineParser.getLineNumber());
				}
				
				final Voice voice = defaultVoiceSupplier.get();
				voice.setName(voiceName);
				voice.setChannel(voices.size());

				voices.add(voice);
				
				parsedVoiceKeys.clear();
			} else {
				// Parse key/value of the last added instrument
				int lastIndex = voices.size() - 1;
				Voice lastAddedVoice = voices.get(lastIndex);
				parseVoiceKeyValue(lastAddedVoice, line, parsedVoiceKeys);
			}
		});

		final Patch patch = defaultValueSupplier.get();
		patch.setName(patchName);
		patch.setVoices(voices);

		return patch;
	}
	
	private void parseVoiceKeyValue(Voice voice, BlockLine line, Set<String> parsedVoiceKeys) throws ParseException {
		String key = line.parseKey();
		if(!parsedVoiceKeys.add(key)) {
			throw new ParseException("Encountered key \"" + key + "\" multiple times in voice \"" + voice.getName() + "\" at line " + lineParser.getLineNumber());
		}
		
		switch(key) {
			case "pitch":
				float pitch = line.parseValueAsFloat();
				voice.setPitch(pitch);
				break;
			case "transpose":
				int transpose = line.parseValueAsInteger();
				voice.setTranspose(transpose);
				break;
			case "instrument":
				String instrument = line.parseValue();
				voice.setInstrument(instrument);
				break;
			case "volume":
				float volume = line.parseValueAsFloat();
				voice.setVolume(volume);
				break;
			case "channel":
				int channel = line.parseValueAsInteger();
				voice.setChannel(channel);
				break;
			default:
				throw new ParseException("Unsupported voice key \"" + key + "\" encountered on line " + lineParser.getLineNumber());	
		}
	}
	
	private String parseStartOfVoice(BlockLine line) throws ParseException {
		String listType = line.parseStartOfListKey();
		if(!listType.equals("VOICE")) {
			throw new ParseException("A start of list with key \"VOICE\" was expected at line " + lineParser.getLineNumber());
		}
		return line.parseStartOfListValue();
	}
}
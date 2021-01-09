package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

import java.util.Arrays;
import java.util.List;

public class ArgumentParser {
	private final List<String> args;
	private boolean debugMode;
	private String inputFile;
	private String outputFile;
	private String bankFile;
	
	public static ParsedArguments parseArguments(String[] args) {
		return new ArgumentParser(args).determineState();
	}
	
	private ArgumentParser(String[] args) {
		this.args = Arrays.asList(args);
	}
	
	public ParsedArguments determineState() {
		// Is mode explicitly set?
		for(int i = 0; i < args.size(); i++) {
			final String arg = args.get(i);
			
			if("".equals(arg.trim())) {
				continue;
			}

			if (arg.equals("--debug")) {
				debugMode = true;
				continue;
			}
			
			if (arg.equals("--help")) {
				return ParsedArguments.showHelp();
			}
			
			if (arg.equals("--bank")) {
				bankFile = parseNext(i++);
				if(bankFile == null) {
					return ParsedArguments.error("No bank file specified", false);
				}
				continue;
			}
			
			if(inputFile == null) {
				this.inputFile = arg;
				continue;
			}
			
			if(outputFile == null) {
				this.outputFile = arg;
				continue;
			}
			
			return ParsedArguments.error("Unknown argument specified: " + arg, false);
		}

		if(inputFile == null && outputFile == null && bankFile == null) {
			return ParsedArguments.showHelp();
		}

		if(inputFile != null && outputFile == null) {
			return ParsedArguments.error("No output file specified", false);
		}


		if(inputFile == null && outputFile == null && bankFile != null) {
			return ParsedArguments.showBankInstruments(bankFile, debugMode);
		}
		
		
		return ParsedArguments.convertSong(inputFile, outputFile, bankFile, debugMode);
	}
	
	private String parseNext(int argIndex) {
		if (++argIndex >= args.size()) {
			return null;
		}
		return args.get(argIndex);
	}
}

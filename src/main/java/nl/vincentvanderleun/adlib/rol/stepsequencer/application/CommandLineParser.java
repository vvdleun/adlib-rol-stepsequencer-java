package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

import java.util.Arrays;
import java.util.List;

public class CommandLineParser {
	private final List<String> args;
	private boolean debugMode;
	private String inputFile;
	private String outputFile;
	private String bankFile;
	
	public static ParsedCommandLine parseArguments(String[] args) {
		return new CommandLineParser(args).parse();
	}
	
	private CommandLineParser(String[] args) {
		this.args = Arrays.asList(args);
	}
	
	public ParsedCommandLine parse() {
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
				return ParsedCommandLine.showHelp();
			}
			
			if (arg.equals("--bank")) {
				bankFile = parseAt(++i);
				if(bankFile == null) {
					return ParsedCommandLine.error("No bank file specified", false);
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
			
			return ParsedCommandLine.error("Unknown argument specified: " + arg, false);
		}

		if(inputFile == null && outputFile == null && bankFile == null) {
			return ParsedCommandLine.showHelp();
		}

		if(inputFile != null && outputFile == null) {
			return ParsedCommandLine.error("No output file specified", false);
		}


		if(inputFile == null && outputFile == null && bankFile != null) {
			return ParsedCommandLine.showBankInstruments(bankFile, debugMode);
		}
		
		
		return ParsedCommandLine.convertSong(inputFile, outputFile, bankFile, debugMode);
	}
	
	private String parseAt(int argIndex) {
		if (argIndex >= args.size()) {
			return null;
		}
		return args.get(argIndex);
	}
}

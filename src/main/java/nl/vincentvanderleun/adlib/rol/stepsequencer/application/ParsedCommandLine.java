package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

public class ParsedCommandLine {
	private final boolean debugMode;
	private final ProgramMode mode;
	private final String inputPath;
	private final String outputPath;
	private final String bankFilePath;
	private final String msg;

	public static ParsedCommandLine showHelp() {
		return new ParsedCommandLine(ProgramMode.SHOW_HELP, false, null, null, null, null);
	}

	public static ParsedCommandLine error(String msg, boolean debugMode) {
		return new ParsedCommandLine(ProgramMode.SHOW_WRONG_USAGE_ERROR, debugMode, null, null, null, msg);
	}

	public static ParsedCommandLine convertSong(String inputPath, String outputPath, String bankFilePath, boolean debugMode) {
		return new ParsedCommandLine(ProgramMode.CONVERT_SONG, debugMode, inputPath, outputPath, bankFilePath, null);
	}

	public static ParsedCommandLine showBankInstruments(String bank, boolean debugMode) {
		return new ParsedCommandLine(ProgramMode.SHOW_BANK_INSTRUMENTS, debugMode, null, null, bank, null);
	}

	private ParsedCommandLine(ProgramMode mode, boolean debugMode, String inputPath, String outputPath, String bankFilePath, String msg) {
		this.mode = mode;
		this.debugMode = debugMode;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.bankFilePath = bankFilePath;
		this.msg = msg;
	}
	
	public ProgramMode getMode() {
		return mode;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public String getInputPath() {
		return inputPath;
	}
	
	public String getOutputPath() {
		return outputPath;
	}

	public String getBankFilePath() {
		return bankFilePath;
	}

	public String getErrorMessage() {
		return msg;
	}
}

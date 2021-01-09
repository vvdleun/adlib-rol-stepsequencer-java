package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

public class ParsedArguments {
	private final boolean debugMode;
	private final State state;
	private final String inputPath;
	private final String outputPath;
	private final String bankFilePath;
	private final String msg;

	public static ParsedArguments showHelp() {
		return new ParsedArguments(State.SHOW_HELP, false, null, null, null, null);
	}

	public static ParsedArguments error(String msg, boolean debugMode) {
		return new ParsedArguments(State.ERROR, debugMode, null, null, null, msg);
	}

	public static ParsedArguments convertSong(String inputPath, String outputPath, String bankFilePath, boolean debugMode) {
		return new ParsedArguments(State.CONVERT_SONG, debugMode, inputPath, outputPath, bankFilePath, null);
	}

	public static ParsedArguments showBankInstruments(String bank, boolean debugMode) {
		return new ParsedArguments(State.SHOW_BANK_INSTRUMENTS, debugMode, null, null, bank, null);
	}

	private ParsedArguments(State state, boolean debugMode, String inputPath, String outputPath, String bankFilePath, String msg) {
		this.state = state;
		this.debugMode = debugMode;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.bankFilePath = bankFilePath;
		this.msg = msg;
	}
	
	public State getState() {
		return state;
	}
	
	public boolean getDebugMode() {
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

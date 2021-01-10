package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandLineParserTests {

	@Test
	public void shouldRecognizeImplicitHelpWhenNoParametersWereProvided() {
		String[] args = { };

		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_HELP, parsedCommandLine.getMode());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeExplicitHelpArgument() {
		String[] args = { "--help" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_HELP, parsedCommandLine.getMode());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParameters() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.CONVERT_SONG, parsedCommandLine.getMode());
		assertEquals("/tmp/input-file-path", parsedCommandLine.getInputPath());
		assertEquals("/tmp/output-file-path", parsedCommandLine.getOutputPath());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedLast() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--bank", "/tmp/bank-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.CONVERT_SONG, parsedCommandLine.getMode());
		assertEquals("/tmp/input-file-path", parsedCommandLine.getInputPath());
		assertEquals("/tmp/output-file-path", parsedCommandLine.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedBetweenInputAndOutput() {
		String[] args = { "/tmp/input-file-path", "--bank", "/tmp/bank-file-path", "/tmp/output-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.CONVERT_SONG, parsedCommandLine.getMode());
		assertEquals("/tmp/input-file-path", parsedCommandLine.getInputPath());
		assertEquals("/tmp/output-file-path", parsedCommandLine.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedBeforeInputAndOutput() {
		String[] args = { "--bank", "/tmp/bank-file-path", "/tmp/input-file-path", "/tmp/output-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.CONVERT_SONG, parsedCommandLine.getMode());
		assertEquals("/tmp/input-file-path", parsedCommandLine.getInputPath());
		assertEquals("/tmp/output-file-path", parsedCommandLine.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertFalse(parsedCommandLine.isDebugMode());
	}
	
	@Test
	public void shouldRecognizeHelpParametersPlacedBetweenInputAndOutputParameters() {
		String[] args = { "/tmp/input-file-path", "--help", "/tmp/output-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_HELP, parsedCommandLine.getMode());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeMissingOutputFile() {
		String[] args = { "/tmp/input-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_WRONG_USAGE_ERROR, parsedCommandLine.getMode());
		assertEquals("No output file specified", parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}

	@Test
	public void shouldRecognizeMissingBankFile() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--bank" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_WRONG_USAGE_ERROR, parsedCommandLine.getMode());
		assertEquals("No bank file specified", parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}
	
	@Test
	public void shouldRecognizeShowInstrumentsInBankFileUtilityOperation() {
		String[] args = { "--bank", "/tmp/bank-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_BANK_INSTRUMENTS, parsedCommandLine.getMode());
		assertEquals("/tmp/bank-file-path", parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertFalse(parsedCommandLine.isDebugMode());
	}
	
	@Test
	public void shouldRecognizeDebugModeInSongConversion() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--debug" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.CONVERT_SONG, parsedCommandLine.getMode());
		assertTrue(parsedCommandLine.isDebugMode());
		assertEquals("/tmp/input-file-path", parsedCommandLine.getInputPath());
		assertEquals("/tmp/output-file-path", parsedCommandLine.getOutputPath());
		assertNull(parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getErrorMessage());
	}
	
	@Test
	public void shouldRecognizeDebugModeInShowInstrumentsInBankFileUtilityOperation() {
		String[] args = { "--debug", "--bank", "/tmp/bank-file-path" };
		
		ParsedCommandLine parsedCommandLine = CommandLineParser.parseArguments(args);
		
		assertEquals(ProgramMode.SHOW_BANK_INSTRUMENTS, parsedCommandLine.getMode());
		assertTrue(parsedCommandLine.isDebugMode());
		assertEquals("/tmp/bank-file-path", parsedCommandLine.getBankFilePath());
		assertNull(parsedCommandLine.getInputPath());
		assertNull(parsedCommandLine.getOutputPath());
		assertNull(parsedCommandLine.getErrorMessage());
	}
}

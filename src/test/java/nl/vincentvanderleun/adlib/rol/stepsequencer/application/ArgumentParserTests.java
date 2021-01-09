package nl.vincentvanderleun.adlib.rol.stepsequencer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArgumentParserTests {

	@Test
	public void shouldRecognizeImplicitHelpWhenNoParametersWereProvided() {
		String[] args = { };

		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_HELP, parsedArguments.getState());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeExplicitHelpArgument() {
		String[] args = { "--help" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_HELP, parsedArguments.getState());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParameters() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.CONVERT_SONG, parsedArguments.getState());
		assertEquals("/tmp/input-file-path", parsedArguments.getInputPath());
		assertEquals("/tmp/output-file-path", parsedArguments.getOutputPath());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedLast() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--bank", "/tmp/bank-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.CONVERT_SONG, parsedArguments.getState());
		assertEquals("/tmp/input-file-path", parsedArguments.getInputPath());
		assertEquals("/tmp/output-file-path", parsedArguments.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedBetweenInputAndOutput() {
		String[] args = { "/tmp/input-file-path", "--bank", "/tmp/bank-file-path", "/tmp/output-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.CONVERT_SONG, parsedArguments.getState());
		assertEquals("/tmp/input-file-path", parsedArguments.getInputPath());
		assertEquals("/tmp/output-file-path", parsedArguments.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeConvertSongParametersWithBankFileSpecifiedBeforeInputAndOutput() {
		String[] args = { "--bank", "/tmp/bank-file-path", "/tmp/input-file-path", "/tmp/output-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.CONVERT_SONG, parsedArguments.getState());
		assertEquals("/tmp/input-file-path", parsedArguments.getInputPath());
		assertEquals("/tmp/output-file-path", parsedArguments.getOutputPath());
		assertEquals("/tmp/bank-file-path", parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertFalse(parsedArguments.getDebugMode());
	}
	
	@Test
	public void shouldRecognizeHelpParametersPlacedBetweenInputAndOutputParameters() {
		String[] args = { "/tmp/input-file-path", "--help", "/tmp/output-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_HELP, parsedArguments.getState());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeMissingOutputFile() {
		String[] args = { "/tmp/input-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_WRONG_USAGE_ERROR, parsedArguments.getState());
		assertEquals("No output file specified", parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}

	@Test
	public void shouldRecognizeMissingBankFile() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--bank" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_WRONG_USAGE_ERROR, parsedArguments.getState());
		assertEquals("No bank file specified", parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}
	
	@Test
	public void shouldRecognizeShowInstrumentsInBankFileUtilityOperation() {
		String[] args = { "--bank", "/tmp/bank-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_BANK_INSTRUMENTS, parsedArguments.getState());
		assertEquals("/tmp/bank-file-path", parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertFalse(parsedArguments.getDebugMode());
	}
	
	@Test
	public void shouldRecognizeDebugModeInSongConversion() {
		String[] args = { "/tmp/input-file-path", "/tmp/output-file-path", "--debug" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.CONVERT_SONG, parsedArguments.getState());
		assertTrue(parsedArguments.getDebugMode());
		assertEquals("/tmp/input-file-path", parsedArguments.getInputPath());
		assertEquals("/tmp/output-file-path", parsedArguments.getOutputPath());
		assertNull(parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getErrorMessage());
	}
	
	@Test
	public void shouldRecognizeDebugModeInShowInstrumentsInBankFileUtilityOperation() {
		String[] args = { "--debug", "--bank", "/tmp/bank-file-path" };
		
		ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);
		
		assertEquals(State.SHOW_BANK_INSTRUMENTS, parsedArguments.getState());
		assertTrue(parsedArguments.getDebugMode());
		assertEquals("/tmp/bank-file-path", parsedArguments.getBankFilePath());
		assertNull(parsedArguments.getInputPath());
		assertNull(parsedArguments.getOutputPath());
		assertNull(parsedArguments.getErrorMessage());
	}
}

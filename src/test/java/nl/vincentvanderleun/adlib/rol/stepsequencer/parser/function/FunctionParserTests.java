package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import nl.vincentvanderleun.adlib.rol.stepsequencer.parser.ParseException;

public class FunctionParserTests {
	private static final long LINE_NUMBER = 1337;

	@Test
	public void shouldParseFunctionCallWithoutParamsInFirstToken() throws Exception {
		final var firstToken = "function()";
		
		FunctionParser parser = createParserWithoutRemainingTokens();

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);
		
		assertEquals("function", actual.getName());
		assertEquals("", actual.getRawArguments());
	}

	@Test
	public void shouldParseCompleteFunctionCallWithOneParamInFirstToken() throws Exception {
		final var firstToken = "function(param)";
		
		FunctionParser parser = createParserWithoutRemainingTokens();

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);
		
		assertEquals("function", actual.getName());
		assertEquals("param", actual.getRawArguments());
	}

	@Test
	public void shouldParseCompleteFunctionCallWithTwoParamsInFirstToken() throws Exception {
		final var firstToken = "function(param1,param2)";
		
		FunctionParser parser = createParserWithoutRemainingTokens();

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);
		
		assertEquals("function", actual.getName());
		assertEquals("param1,param2", actual.getRawArguments());
	}

	@Test
	public void shouldParseCompleteFunctionCallWithTwoParamsInMultipleTokens() throws Exception {
		final var firstToken = "function(param1,";
		
		FunctionParser parser = createParserWithRemainingTokens(" param2)");

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);
		
		assertEquals("function", actual.getName());
		assertEquals("param1,param2", actual.getRawArguments());
	}

	@Test
	public void shouldParseCompleteFunctionCallWithThreeParamsInMultipleTokensAndWithWhitespace() throws Exception {
		final var firstToken = "function(";
		
		FunctionParser parser = createParserWithRemainingTokens("param1  ,  param2,    param3\t   )   ");

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);

		assertEquals("function", actual.getName());
		assertEquals("param1,param2,param3", actual.getRawArguments());
	}

	@Test
	public void shouldThrowWhenFunctionInOnlySpecifiedTokenIsNotClosed() throws Exception {
		assertThrows(ParseException.class, () -> {
			final var firstToken = "function(";

			FunctionParser parser = createParserWithoutRemainingTokens();

			parser.parse(firstToken, LINE_NUMBER);
		});
	}

	@Test
	public void shouldThrowWhenFunctionInRemainingTokensIsNotClosed() throws Exception {
		assertThrows(ParseException.class, () -> {
			var firstToken = "function(";

			FunctionParser parser = createParserWithRemainingTokens("param1, param2, butNotClosed");

			parser.parse(firstToken, LINE_NUMBER);
		});
	}

	@Test
	public void shouldThrowWhenFunctionInSpecifiedTokensIsWronglyClosedInsideAParameter() throws Exception {
		assertThrows(ParseException.class, () -> {
			var firstToken = "function(";

			FunctionParser parser = createParserWithRemainingTokens("param1),param2");

			parser.parse(firstToken, LINE_NUMBER);
		});
	}

	@Test
	public void shouldReturnNullWhenTokenDoesNotAppearToContainAFunctionCall() throws Exception {
		var firstToken = "not-a-function-because-this-token-doesn't-have-an-open-parentheses-character";

		FunctionParser parser = createParserWithRemainingTokens("(do-not-be-fooled, this-are-not-considered-parameters)");

		ParsableFunction actual = parser.parse(firstToken, LINE_NUMBER);
		
		assertNull(actual);
	}
	
	private FunctionParser createParserWithoutRemainingTokens() {
		return createParserWithRemainingTokens("");
	}

	private FunctionParser createParserWithRemainingTokens(String remainingTokens) {
		Scanner scanner = new Scanner(remainingTokens);
		return new FunctionParser(scanner);
	}
}

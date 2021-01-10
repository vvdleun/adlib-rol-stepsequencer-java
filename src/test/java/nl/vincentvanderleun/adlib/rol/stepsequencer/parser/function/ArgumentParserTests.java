package nl.vincentvanderleun.adlib.rol.stepsequencer.parser.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArgumentParserTests {
	private static final String FUNCTION_NAME = "function";
	private static final long LINE_NUMBER = 1337;
	
	@Test
	public void shouldRecognizeEmptyArguments() {
		ArgumentParser parser = createArgumentParser("");
		
		boolean actual = parser.hasMoreArguments();
		
		assertFalse(actual);
	}

	@Test
	public void shouldRecognizeNoArguments() {
		ArgumentParser parser = createArgumentParser(null);
		
		boolean actual = parser.hasMoreArguments();
		
		assertFalse(actual);
	}

	@Test
	public void shouldRecognizeOneArgument() throws Exception {
		ArgumentParser parser = createArgumentParser("param1");
		
		assertTrue(parser.hasMoreArguments());
		
		String actual = parser.parseNextArgument();
		
		assertFalse(parser.hasMoreArguments());
		
		assertEquals("param1", actual);
	}

	@Test
	public void shouldRecognizeThreeArguments() throws Exception {
		ArgumentParser parser = createArgumentParser("param1, param2, param3");
		
		assertTrue(parser.hasMoreArguments());
		
		String actual1 = parser.parseNextArgument();

		assertTrue(parser.hasMoreArguments());

		String actual2 = parser.parseNextArgument();

		assertTrue(parser.hasMoreArguments());

		String actual3 = parser.parseNextArgument();
		
		assertFalse(parser.hasMoreArguments());
		
		assertEquals("param1", actual1);
		assertEquals("param2", actual2);
		assertEquals("param3", actual3);
	}
	
	@Test
	public void shouldParseArgumentAsInteger() throws Exception {
		ArgumentParser parser = createArgumentParser("123");
		
		assertTrue(parser.hasMoreArguments());
		
		int actual = parser.parseNextArgumentAsInteger();
		
		assertFalse(parser.hasMoreArguments());
		
		assertEquals(123, actual);
	}

	@Test
	public void shouldParseArgumentAsFloat() throws Exception {
		ArgumentParser parser = createArgumentParser("1.0");
		
		assertTrue(parser.hasMoreArguments());
		
		float actual = parser.parseNextArgumentAsFloat();
		
		assertFalse(parser.hasMoreArguments());
		
		assertEquals(1.0f, actual);
	}

	@Test
	public void shouldParseArgumentAsBoolean() throws Exception {
		ArgumentParser parser = createArgumentParser("true");
		
		assertTrue(parser.hasMoreArguments());
		
		boolean actual = parser.parseNextArgumentAsBoolean();
		
		assertFalse(parser.hasMoreArguments());
		
		assertEquals(true, actual);
	}
	
	private ArgumentParser createArgumentParser(String rawArguments) {
		return new ArgumentParser(FUNCTION_NAME, rawArguments, LINE_NUMBER);
	}
}

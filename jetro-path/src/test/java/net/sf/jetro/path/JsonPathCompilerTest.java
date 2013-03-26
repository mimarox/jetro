package net.sf.jetro.path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import net.sf.testng.databinding.TestOutput;

import org.testng.annotations.Test;

@DataBinding
public class JsonPathCompilerTest {
	private JsonPathCompiler compiler = new JsonPathCompiler();

	@Test
	public void shouldCompile(@TestInput(name = "jsonPath") final String jsonPath) {
		String actual = compiler.compile(jsonPath).toString();
		assertEquals(actual, jsonPath);
	}

	@Test
	public void shouldNotCompile(@TestInput(name = "jsonPath") final String jsonPath,
			@TestOutput(name = "errorMessage") final String errorMessage) {
		try {
			compiler.compile(jsonPath);
			fail(jsonPath + " compiled, but shouldn't have");
		} catch (JsonPathCompilerException e) {
			assertEquals(e.getMessage(), errorMessage);
		}
	}
}
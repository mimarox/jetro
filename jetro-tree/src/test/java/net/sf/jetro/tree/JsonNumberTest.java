package net.sf.jetro.tree;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class JsonNumberTest {

	@Test
	public void shouldHavePathsAfterDeepCopying() {
		JsonPath jsonPath = JsonPath.compile("$[0]");
		
		JsonNumber jsonNumber = new JsonNumber(jsonPath, 1);
		JsonNumber deepCopied = jsonNumber.deepCopy();
		
		assertEquals(deepCopied, jsonNumber);
		assertTrue(deepCopied != jsonNumber);
		
		Optional<JsonType> optional = deepCopied.getElementAt(jsonPath);
		
		assertTrue(optional.isPresent());
		assertTrue(optional.get() == deepCopied);
	}
}

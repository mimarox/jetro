package net.sf.jetro.tree;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class JsonStringTest {

	@Test
	public void shouldHavePathsAfterDeepCopying() {
		JsonPath jsonPath = JsonPath.compile("$[0]");
		
		JsonString jsonString = new JsonString(jsonPath, "value");
		JsonString deepCopied = jsonString.deepCopy();
		
		assertEquals(deepCopied, jsonString);
		assertTrue(deepCopied != jsonString);
		
		Optional<JsonType> optional = deepCopied.getElementAt(jsonPath);
		
		assertTrue(optional.isPresent());
		assertTrue(optional.get() == deepCopied);
	}
}

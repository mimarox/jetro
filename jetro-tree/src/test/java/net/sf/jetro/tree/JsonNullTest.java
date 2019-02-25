package net.sf.jetro.tree;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class JsonNullTest {

	@Test
	public void shouldHavePathsAfterDeepCopying() {
		JsonPath jsonPath = JsonPath.compile("$[0]");
		
		JsonNull jsonNull = new JsonNull(jsonPath);
		JsonNull deepCopied = jsonNull.deepCopy();
		
		assertEquals(deepCopied, jsonNull);
		assertTrue(deepCopied != jsonNull);
		
		Optional<JsonType> optional = deepCopied.getElementAt(jsonPath);
		
		assertTrue(optional.isPresent());
		assertTrue(optional.get() == deepCopied);
	}
}

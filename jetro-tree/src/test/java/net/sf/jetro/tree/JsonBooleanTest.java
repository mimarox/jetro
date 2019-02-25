package net.sf.jetro.tree;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class JsonBooleanTest {
	
	@Test
	public void shouldHavePathsAfterDeepCopying() {
		JsonPath jsonPath = JsonPath.compile("$[0]");
		
		JsonBoolean jsonBoolean = new JsonBoolean(jsonPath, true);
		JsonBoolean deepCopied = jsonBoolean.deepCopy();
		
		assertEquals(deepCopied, jsonBoolean);
		assertTrue(deepCopied != jsonBoolean);
		
		Optional<JsonType> optional = deepCopied.getElementAt(jsonPath);
		
		assertTrue(optional.isPresent());
		assertTrue(optional.get() == deepCopied);
	}
}

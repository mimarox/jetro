package net.sf.jetro.patch.pointer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

public class JsonPointerTest {
	
	@Test
	@DataBinding(propertiesPrefix = "mapJsonPathToJsonPointer")
	public void shouldCreateFromJsonPath(@TestInput(name = "jsonPath") final String jsonPath,
										 @TestInput(name = "jsonPointer") final String jsonPointer) {
		JsonPath path = JsonPath.compile(jsonPath);
		
		JsonPointer actual = JsonPointer.fromJsonPath(path);
		JsonPointer expected = JsonPointer.compile(jsonPointer);
		
		assertNotNull(actual);
		assertNotNull(expected);
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapJsonPathToJsonPointer")
	public void shouldConvertToJsonPath(@TestInput(name = "jsonPath") final String jsonPath,
										@TestInput(name = "jsonPointer") final String jsonPointer) {
		JsonPointer pointer = JsonPointer.compile(jsonPointer);
		
		assertNotNull(pointer);
		
		JsonPath actual = pointer.toJsonPath();
		JsonPath expected = JsonPath.compile(jsonPath);
		
		assertNotNull(actual);
		assertNotNull(expected);
		assertEquals(actual, expected);
	}
}

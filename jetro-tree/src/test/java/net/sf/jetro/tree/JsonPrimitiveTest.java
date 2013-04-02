package net.sf.jetro.tree;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import net.sf.jetro.path.JsonPath;

import org.testng.annotations.Test;

public class JsonPrimitiveTest {

	@Test
	public void shouldRenderItself() {
		JsonPrimitive<String> jsonPrimitive = new JsonString("happy-happy");

		// call toJson on JsonPrimitive
		String actual = jsonPrimitive.toJson();
		String expected = "\"happy-happy\"";

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldRenderItselfWithRenderer() {
		JsonPrimitive<String> jsonPrimitive = new JsonString();

		String expected = String.valueOf(System.currentTimeMillis());
		JsonRenderer mockedRenderer = mock(JsonRenderer.class);
		when(mockedRenderer.render(any(JsonElement.class))).thenReturn(expected);

		// call toJson on JsonPrimitive with a JsonRenderer
		String actual = jsonPrimitive.toJson(mockedRenderer);
		verify(mockedRenderer).render(jsonPrimitive);

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldGetChildElement() {
		// Setup JSON tree representing {"foo":"happy-happy-joy-joy"}
		JsonObject root = new JsonObject();
		JsonProperty foo = new JsonProperty("foo");

		JsonPrimitive<String> jsonPrimitive = new JsonString();
		jsonPrimitive.setValue("happy-happy-joy-joy");
		foo.setValue(jsonPrimitive);
		root.add(foo);

		// define path for third element
		JsonPath jsonPath = JsonPath.compile("$.foo[2]");

		// call getElementAt on JsonPrimitive
		String actual = jsonPrimitive.getElementAt(jsonPath).toString();
		String expected = "{\"foo\":\"happy-happy-joy-joy\"}";

		// Assert
		assertEquals(actual, expected);
	}

	// TODO put a wrong path in element then test that NoSuchElementException is actually thrown	
}
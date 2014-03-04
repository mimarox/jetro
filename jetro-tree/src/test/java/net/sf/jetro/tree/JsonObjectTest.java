package net.sf.jetro.tree;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import net.sf.jetro.path.JsonPath;
import org.testng.annotations.Test;

import java.util.NoSuchElementException;

public class JsonObjectTest {

	@Test
	public void shouldRenderItself() {
		JsonObject jsonObject = new JsonObject();

		JsonProperty jsonPropertyA = new JsonProperty("A");
		jsonPropertyA.setValue(new JsonString("hello"));
		jsonObject.add(jsonPropertyA);

		JsonProperty jsonPropertyB = new JsonProperty("B");
		jsonPropertyB.setValue(new JsonNumber(44));
		jsonObject.add(jsonPropertyB);

		JsonProperty jsonPropertyC = new JsonProperty("C");
		jsonPropertyC.setValue(new JsonString("goodbye"));
		jsonObject.add(jsonPropertyC);

		// call toJson on JsonObject
		String actual = jsonObject.toJson();
		String expected = "{\"A\":\"hello\",\"B\":44,\"C\":\"goodbye\"}";

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldRenderItselfWithRenderer() {
		JsonObject jsonObject = new JsonObject();

		String expected = String.valueOf(System.currentTimeMillis());
		JsonRenderer mockedRenderer = mock(JsonRenderer.class);
		when(mockedRenderer.render(any(JsonElement.class))).thenReturn(expected);

		// call toJson on JsonObject with a JsonRenderer
		String actual = jsonObject.toJson(mockedRenderer);
		verify(mockedRenderer).render(jsonObject);

		// Assert
		assertEquals(actual, expected);
	}

	/**
	 * Test that getChildElementAt returns String representing the correct element.
	 *
	 */
	// TODO when internal path setting is implemented remove the expectedExceptions directive
	@Test(expectedExceptions = NoSuchElementException.class)
	public void shouldGetChildElementAt() {
		// Setup JSON tree representing {"foo":[1,"two",{"bar":true}]}
		JsonObject jsonObject = new JsonObject();
		JsonProperty foo = new JsonProperty("foo");
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonNumber(1));
		jsonArray.add(new JsonString("two"));
		JsonObject barObject = new JsonObject();
		JsonProperty bar = new JsonProperty("bar");
		bar.setValue(new JsonBoolean(true));
		barObject.add(bar);
		jsonArray.add(barObject);
		foo.setValue(jsonArray);
		jsonObject.add(foo);

		// define path for third element
		JsonPath jsonPath = JsonPath.compile("$.foo[2]");

		// call getElementAt on JsonArray
		String actual = jsonObject.getElementAt(jsonPath).toString();
		String expected = "{\"bar\":true}";

		// Assert
		assertEquals(actual, expected);
	}
}
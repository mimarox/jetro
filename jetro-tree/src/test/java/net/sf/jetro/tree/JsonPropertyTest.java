package net.sf.jetro.tree;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class JsonPropertyTest {

	@Test
	public void shouldRenderItself() {
		JsonProperty jsonProperty = new JsonProperty("alpha");
		jsonProperty.setValue(new JsonString("apple"));

		// call toJson on JsonProperty
		String actual = jsonProperty.toJson();
		String expected = "\"alpha\":\"apple\"";

		// Assert
		assertEquals(actual, expected);
	}

	public void shouldRenderItselfWithRenderer() {
		JsonProperty jsonProperty = new JsonProperty("alpha");

		String expected = String.valueOf(System.currentTimeMillis());
		JsonRenderer mockedRenderer = mock(JsonRenderer.class);
		when(mockedRenderer.render(any(JsonElement.class))).thenReturn(expected);

		// call toJson on JsonObject with a JsonRenderer
		String actual = jsonProperty.toJson(mockedRenderer);
		verify(mockedRenderer).render(jsonProperty);

		// Assert
		assertEquals(actual, expected);
	}
}
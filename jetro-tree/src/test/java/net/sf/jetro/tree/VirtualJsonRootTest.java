package net.sf.jetro.tree;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import net.sf.jetro.tree.renderer.JsonRenderer;
import org.testng.annotations.Test;

public class VirtualJsonRootTest {

	@Test
	public void shouldRenderItself() {
		VirtualJsonRoot virtualJsonRoot = new VirtualJsonRoot();
		JsonObject jsonObject = new JsonObject();
		JsonProperty jsonProperty = new JsonProperty("virtual-root");
		jsonProperty.setValue(new JsonString("virtually happy"));
		jsonObject.add(jsonProperty);
		virtualJsonRoot.add(jsonObject);

		// call toJson on JsonArray
		String actual = virtualJsonRoot.toJson();
		String expected = "{\"virtual-root\":\"virtually happy\"}";

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldRenderItselfWithRenderer() {
		VirtualJsonRoot virtualJsonRoot = new VirtualJsonRoot();

		String expected = String.valueOf(System.currentTimeMillis());
		JsonRenderer mockedRenderer = mock(JsonRenderer.class);
		when(mockedRenderer.render(any(JsonElement.class))).thenReturn(expected);

		// call toJson on JsonArray with a JsonRenderer
		String actual = virtualJsonRoot.toJson(mockedRenderer);
		verify(mockedRenderer).render(virtualJsonRoot);

		// Assert
		assertEquals(actual, expected);
	}
}
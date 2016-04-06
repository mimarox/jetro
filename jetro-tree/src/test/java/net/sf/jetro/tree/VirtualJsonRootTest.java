/*
 * #%L
 * Jetro Tree
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
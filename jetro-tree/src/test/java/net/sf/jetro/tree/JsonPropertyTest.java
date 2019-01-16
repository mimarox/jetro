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
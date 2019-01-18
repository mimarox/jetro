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
import net.sf.jetro.path.JsonPath;

import net.sf.jetro.tree.renderer.JsonRenderer;
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
		// define path for third element
		JsonPath jsonPath = JsonPath.compile("$.foo[2]");

		JsonPrimitive<String> jsonString = new JsonPrimitive<String>(jsonPath, "happy") {

			@Override
			public JsonType deepCopy() {
				return this;
			}};

		// call getElementAt on JSON primitive
		String actual = ((JsonPrimitive<String>) jsonString.getElementAt(JsonPath.compile("$.foo[2]"))).getValue();
		String expected = "happy";

		// Assert
		assertEquals(actual, expected);
	}

	// TODO put a wrong path in element then test that NoSuchElementException is actually thrown	
}
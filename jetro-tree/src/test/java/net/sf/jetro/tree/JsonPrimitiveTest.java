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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

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
	public void shouldGetChildElement() {
		// define path for third element
		JsonPath jsonPath = JsonPath.compile("$.foo[2]");

		JsonPrimitive<String> jsonString = new JsonPrimitive<String>(jsonPath, "happy") {

			@Override
			public JsonType deepCopy() {
				return this;
			}};

		// call getElementAt on JSON primitive
		@SuppressWarnings("unchecked")
		String actual = ((JsonPrimitive<String>) jsonString.getElementAt(jsonPath).get()).getValue();
		String expected = "happy";

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldGetSamePrimitiveAtDifferentPaths() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		Optional<JsonType> optional1 =
				jsonArray.getElementAt(JsonPath.compile("$[1].jsonString"));
		
		assertTrue(optional1.isPresent());
		assertTrue(optional1.get() == jsonString);
		
		Optional<JsonType> optional2 =
				jsonArray.getElementAt(JsonPath.compile("$[0]"));
		
		assertTrue(optional2.isPresent());
		assertTrue(optional2.get() == jsonString);
	}
	
	// TODO put a wrong path in element then test that NoSuchElementException is actually thrown	
}
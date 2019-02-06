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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.JsonRenderer;

public class JsonArrayTest {

	/**
	 * Test that toJson on JsonArray actually returns String correctly representing the JsonArray.
	 */
	@Test
	public void shouldRenderItself() {
		// simple array of string types
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonString("hello"));
		jsonArray.add(new JsonString("goodbye"));
		jsonArray.add(new JsonString("thank you"));
		jsonArray.add(new JsonString("welcome"));

		// call toJson on JsonArray
		String actual = jsonArray.toJson();
		String expected = "[\"hello\",\"goodbye\",\"thank you\",\"welcome\"]";

		// Assert
		assertEquals(actual, expected);
	}

	/**
	 * Test for toJson(JsonRenderer r) using a mocked JsonRenderer. 
	 */
	@Test
	public void shouldRenderItselfWithRenderer() {
		JsonArray jsonArray = new JsonArray();

		String expected = String.valueOf(System.currentTimeMillis());
		JsonRenderer mockedRenderer = mock(JsonRenderer.class);
		when(mockedRenderer.render(any(JsonElement.class))).thenReturn(expected);

		// call toJson on JsonArray with a JsonRenderer
		String actual = jsonArray.toJson(mockedRenderer);
		verify(mockedRenderer).render(jsonArray);

		// Assert
		assertEquals(actual, expected);
	}

	/**
	 * Test that getChildElementAt returns String representing the correct element.
	 */
	@Test
	public void shouldGetChildElementAt() {
		// Setup JSON tree representing [1,"two",{"bar":true}]
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonNumber(1));
		jsonArray.add(new JsonString("two"));
		JsonObject barObject = new JsonObject();
		JsonProperty bar = new JsonProperty("bar");
		bar.setValue(new JsonBoolean(true));
		barObject.add(bar);
		jsonArray.add(barObject);

		//Recalculate Tree Paths
		jsonArray.recalculateTreePaths();
		
		// define path for third element
		JsonPath jsonPath = JsonPath.compile("$[2]");

		// call getElementAt on JsonArray
		String actual = jsonArray.getElementAt(jsonPath).get().toJson();
		String expected = "{\"bar\":true}";

		// Assert
		assertEquals(actual, expected);
	}

	@Test
	public void shouldGetSameChildOfArrayAtDifferentPaths() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonString);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("jsonObject", jsonObject));
		root.add(new JsonProperty("jsonArray", jsonArray));
		
		root.recalculateTreePaths();
		
		Optional<JsonType> optional1 =
				root.getElementAt(JsonPath.compile("$.jsonArray[0]"));
		
		assertTrue(optional1.isPresent());
		assertTrue(optional1.get() == jsonString);
		
		Optional<JsonType> optional2 =
				root.getElementAt(JsonPath.compile("$.jsonArray[1]"));
		
		assertTrue(optional2.isPresent());
		assertTrue(optional2.get() == jsonString);
		
		Optional<JsonType> optional3 =
				root.getElementAt(JsonPath.compile("$.jsonObject.jsonArray[0]"));
		
		assertTrue(optional3.isPresent());
		assertTrue(optional3.get() == jsonString);
	}

	@Test
	public void shouldGetSameArrayAtDifferentPaths() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("jsonObject", jsonObject));
		root.add(new JsonProperty("jsonArray", jsonArray));
		
		root.recalculateTreePaths();
		
		Optional<JsonType> optional1 =
				root.getElementAt(JsonPath.compile("$.jsonArray"));
		
		assertTrue(optional1.isPresent());
		assertTrue(optional1.get() == jsonArray);
		
		Optional<JsonType> optional2 =
				root.getElementAt(JsonPath.compile("$.jsonObject.jsonArray"));
		
		assertTrue(optional2.isPresent());
		assertTrue(optional2.get() == jsonArray);
	}

	@Test
	public void shouldDeepCopyWithPaths() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonString("value"));
		jsonArray.add(new JsonNumber(1));
		
		JsonArray root = new JsonArray();
		root.add(jsonArray);
		
		root.recalculateTreePaths();
		
		JsonArray deepCopied = root.deepCopy();
		
		assertEquals(deepCopied, root);
		assertTrue(deepCopied != root);
		
		Optional<JsonType> optional = deepCopied.getElementAt(
				JsonPath.compile("$[0][0]"));
		
		assertTrue(optional.isPresent());
		assertEquals(optional.get(), new JsonString("value"));
	}
	
	@Test
	public void shouldRemoveElementAtFromArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray innerArray = new JsonArray();
		innerArray.add(jsonBoolean);
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		
		outerArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0][0]");
		
		Optional<JsonType> optionalBeforeRemove = outerArray.getElementAt(path);
		
		assertTrue(optionalBeforeRemove.isPresent());
		assertTrue(optionalBeforeRemove.get() == jsonBoolean);
		
		boolean removed = outerArray.removeElementAt(path);
		
		assertTrue(removed);
		
		Optional<JsonType> optionalAfterRemove = outerArray.getElementAt(path);
		
		assertFalse(optionalAfterRemove.isPresent());
	}
	
	@Test
	public void shouldRemoveElementAtFromObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean");
		
		Optional<JsonType> optionalBeforeRemove = jsonArray.getElementAt(path);
		
		assertTrue(optionalBeforeRemove.isPresent());
		assertTrue(optionalBeforeRemove.get() == jsonBoolean);
		
		boolean removed = jsonArray.removeElementAt(path);
		
		assertTrue(removed);
		
		Optional<JsonType> optionalAfterRemove = jsonArray.getElementAt(path);
		
		assertFalse(optionalAfterRemove.isPresent());
	}
	
	@Test
	public void shouldNotRemoveElementAtFromPrimitive() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean[0]");
		
		Optional<JsonType> optionalBeforeRemove = jsonArray.getElementAt(path);
		
		assertFalse(optionalBeforeRemove.isPresent());
		
		boolean removed = jsonArray.removeElementAt(path);
		
		assertFalse(removed);
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingElement() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.removeElementAt(JsonPath.compile("$[0]")));
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingProperty() {
		JsonObject jsonObject = new JsonObject();
		
		JsonArray jsonArray = new JsonArray(Arrays.asList(jsonObject));
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.removeElementAt(JsonPath.compile("$[0].property")));
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingPath() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.removeElementAt(JsonPath.compile("$[0][0]")));
	}
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray"})
	public void shouldRemoveElementOnlyAtFromArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(jsonBoolean);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		Optional<JsonType> optionalBeforeRemove =
				firstArray.getElementAt(JsonPath.compile("$[0][0][0]"));
		
		assertTrue(optionalBeforeRemove.isPresent());
		assertEquals(optionalBeforeRemove.get(), jsonBoolean);
		
		boolean removed = firstArray.removeElementAt(JsonPath.compile("$[1][0]"));
		
		assertTrue(removed);
		
		Optional<JsonType> optionalAfterRemove =
				firstArray.getElementAt(JsonPath.compile("$[0][0][0]"));
		
		assertTrue(optionalAfterRemove.isPresent());
		assertEquals(optionalAfterRemove.get(), jsonBoolean);
	}
}

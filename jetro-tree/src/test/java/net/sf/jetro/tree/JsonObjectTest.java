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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.JsonRenderer;

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
	 * Test that getElementAt returns String representing the correct element.
	 *
	 */
	@Test
	public void shouldGetElementAt() {
		// Setup JSON tree representing {"foo":[1,"two",{"bar":true}]}
		JsonObject barObject = new JsonObject();
		barObject.add(new JsonProperty("bar", true));
		
		JsonArray fooArray = new JsonArray();
		fooArray.add(new JsonNumber(1));
		fooArray.add(new JsonString("two"));
		fooArray.add(barObject);

		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("foo", fooArray));
		
		// Recalculate Json Tree Path
		fooArray = fooArray.deepCopy();
		fooArray.recalculateTreePaths();
		jsonObject.recalculateTreePaths();
		
		// Define path for third element
		JsonPath jsonPath1 = JsonPath.compile("$.foo[2]");

		// Call getElementAt on JsonArray
		String actual1 = jsonObject.getElementAt(jsonPath1).get().toJson();
		String expected = "{\"bar\":true}";

		// Assert
		assertEquals(actual1, expected);

		// Define path for third element
		JsonPath jsonPath2 = JsonPath.compile("$[2]");

		// Call getElementAt on JsonArray
		String actual2 = fooArray.getElementAt(jsonPath2).get().toJson();

		// Assert
		assertEquals(actual2, expected);
	}
	
	@Test
	public void shouldRetainAllByKey() {
		//prepare JsonObject
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.add(new JsonProperty("a", 1));
		jsonObject.add(new JsonProperty("b", 2));
		jsonObject.add(new JsonProperty("c", 3));
		jsonObject.add(new JsonProperty("d", 4));
		
		jsonObject.retainAllByKey(Arrays.asList("a", "b"));
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", 1));
		expected.add(new JsonProperty("b", 2));
		
		assertEquals(jsonObject, expected);
	}
	
	@Test
	public void shouldRemoveAllByKey() {
		//prepare JsonObject
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.add(new JsonProperty("a", 1));
		jsonObject.add(new JsonProperty("b", 2));
		jsonObject.add(new JsonProperty("c", 3));
		jsonObject.add(new JsonProperty("d", 4));
		
		jsonObject.removeAllByKey(Arrays.asList("a", "b"));
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("c", 3));
		expected.add(new JsonProperty("d", 4));
		
		assertEquals(jsonObject, expected);
	}
	
	@Test
	public void shouldContainAllKeys() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("a", 1));
		jsonObject.add(new JsonProperty("b", 2));
		jsonObject.add(new JsonProperty("c", 3));
		
		assertTrue(jsonObject.containsAllKeys(Arrays.asList("a", "b")));
	}
	
	@Test
	public void shouldNotThrowNPEWhenGettingNonExistingValue() {
		assertNull(new JsonObject().get("key"));
	}

	@Test
	public void shouldGetSameChildOfObjectAtDifferentPaths() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonObject);
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("jsonObject", jsonObject));
		root.add(new JsonProperty("jsonArray", jsonArray));
		
		root.recalculateTreePaths();
		
		Optional<JsonType> optional1 =
				root.getElementAt(JsonPath.compile("$.jsonArray[1].jsonString"));
		
		assertTrue(optional1.isPresent());
		assertTrue(optional1.get() == jsonString);
		
		Optional<JsonType> optional2 =
				root.getElementAt(JsonPath.compile("$.jsonArray[0]"));
		
		assertTrue(optional2.isPresent());
		assertTrue(optional2.get() == jsonString);
		
		Optional<JsonType> optional3 =
				root.getElementAt(JsonPath.compile("$.jsonObject.jsonString"));
		
		assertTrue(optional3.isPresent());
		assertTrue(optional3.get() == jsonString);
	}
	
	@Test
	public void shouldGetSameObjectAtDifferentPaths() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonObject);
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("jsonObject", jsonObject));
		root.add(new JsonProperty("jsonArray", jsonArray));
		
		root.recalculateTreePaths();
		
		Optional<JsonType> optional1 =
				root.getElementAt(JsonPath.compile("$.jsonArray[1]"));
		
		assertTrue(optional1.isPresent());
		assertTrue(optional1.get() == jsonObject);
		
		Optional<JsonType> optional2 =
				root.getElementAt(JsonPath.compile("$.jsonObject"));
		
		assertTrue(optional2.isPresent());
		assertTrue(optional2.get() == jsonObject);
	}
	
	@Test
	public void shouldDeepCopyWithPaths() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("stringKey", "value"));
		jsonObject.add(new JsonProperty("numberKey", 1));
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("objectKey", jsonObject));
		
		root.recalculateTreePaths();
		
		JsonObject deepCopied = root.deepCopy();
		
		assertEquals(deepCopied, root);
		assertTrue(deepCopied != root);
		
		Optional<JsonType> optional = deepCopied.getElementAt(
				JsonPath.compile("$.objectKey.stringKey"));
		
		assertTrue(optional.isPresent());
		assertEquals(optional.get(), new JsonString("value"));
	}
	
	@Test
	public void shouldRemoveElementAtFromObject() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("innerObject", innerObject));
		
		outerObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerObject.jsonString");
		
		Optional<JsonType> optionalBeforeRemove = outerObject.getElementAt(path);
		
		assertTrue(optionalBeforeRemove.isPresent());
		assertEquals(optionalBeforeRemove.get(), jsonString);
		
		boolean removed = outerObject.removeElementAt(path);
		
		assertTrue(removed);
		
		Optional<JsonType> optionalAfterRemove = outerObject.getElementAt(path);
		
		assertFalse(optionalAfterRemove.isPresent());
	}
	
	@Test
	public void shouldRemoveElementAtFromArray() {
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonArray[0]");
		
		Optional<JsonType> optionalBeforeRemove = jsonObject.getElementAt(path);
		
		assertTrue(optionalBeforeRemove.isPresent());
		assertEquals(optionalBeforeRemove.get(), jsonString);
		
		boolean removed = jsonObject.removeElementAt(path);
		
		assertTrue(removed);
		
		Optional<JsonType> optionalAfterRemove = jsonObject.getElementAt(path);
		
		assertFalse(optionalAfterRemove.isPresent());
	}
	
	@Test
	public void shouldNotRemoveElementAtFromPrimitive() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonBoolean);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonArray[0][0]");
		
		Optional<JsonType> optionalBeforeRemove = jsonObject.getElementAt(path);
		
		assertFalse(optionalBeforeRemove.isPresent());
		
		boolean removed = jsonObject.removeElementAt(path);
		
		assertFalse(removed);
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingElement() {
		JsonArray jsonArray = new JsonArray();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.removeElementAt(JsonPath.compile("$.jsonArray[0]")));
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingProperty() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.removeElementAt(JsonPath.compile("$.property")));
	}
	
	@Test
	public void shouldNotRemoveElementAtWithNonexistingPath() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.removeElementAt(JsonPath.compile("$.foo.bar")));
	}
}

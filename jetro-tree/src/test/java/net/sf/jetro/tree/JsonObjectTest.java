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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonObject.JsonProperties;
import net.sf.jetro.tree.renderer.JsonRenderer;

public class JsonObjectTest {
	private class JsonTypeVerifier {
		private final JsonObject jsonObject;
		private final JsonPath path;
		
		private JsonTypeVerifier(final JsonObject jsonObject, final JsonPath path) {
			Objects.requireNonNull(jsonObject);
			Objects.requireNonNull(path);
			
			this.jsonObject = jsonObject;
			this.path = path;
		}
		
		private void is(final JsonType element) {
			Optional<JsonType> optionalElement = jsonObject.getElementAt(path);
			
			assertTrue(optionalElement.isPresent());
			assertTrue(optionalElement.get() == element);
		}
		
		private void equals(final JsonType element) {
			Optional<JsonType> optionalElement = jsonObject.getElementAt(path);
			
			assertTrue(optionalElement.isPresent());
			assertEquals(optionalElement.get(), element);
		}
	}
	
	private class JsonObjectHolder {
		private final JsonObject jsonObject;
		
		private JsonObjectHolder(final JsonObject jsonObject) {
			Objects.requireNonNull(jsonObject);
			
			this.jsonObject = jsonObject;
		}
		
		private JsonTypeVerifier at(final JsonPath path) {
			return new JsonTypeVerifier(jsonObject, path);
		}
	}
	
	private JsonObjectHolder verifyElementFrom(final JsonObject jsonObject) {
		return new JsonObjectHolder(jsonObject);
	}

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
		
		jsonObject.removeAllByKeys(Arrays.asList("a", "b"));
		
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
		
		verifyElementFrom(root).at(JsonPath.compile("$.jsonArray[1].jsonString")).is(jsonString);
		verifyElementFrom(root).at(JsonPath.compile("$.jsonArray[0]")).is(jsonString);
		verifyElementFrom(root).at(JsonPath.compile("$.jsonObject.jsonString")).is(jsonString);
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
		
		verifyElementFrom(root).at(JsonPath.compile("$.jsonArray[1]")).is(jsonObject);
		verifyElementFrom(root).at(JsonPath.compile("$.jsonObject")).is(jsonObject);
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
		
		verifyElementFrom(deepCopied).at(
				JsonPath.compile("$.objectKey.stringKey")).equals(new JsonString("value"));
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
		
		verifyElementFrom(outerObject).at(path).is(jsonString);
		
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
		
		verifyElementFrom(jsonObject).at(path).is(jsonString);
		
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

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject thirdObject = new JsonObject();
		thirdObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		thirdObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonObject secondObject = new JsonObject();
		secondObject.add(new JsonProperty("thirdObject", thirdObject));
		
		JsonObject firstObject = new JsonObject();
		firstObject.add(new JsonProperty("secondObject", secondObject));
		firstObject.add(new JsonProperty("thirdObject", thirdObject));
		
		firstObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.secondObject.thirdObject.jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$.thirdObject.jsonString");
		
		verifyElementFrom(firstObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstObject).at(stringPath).is(jsonString);
		
		boolean removed = firstObject.removeElementAt(
				JsonPath.compile("$.thirdObject.jsonBoolean"));
		assertTrue(removed);
		
		verifyElementFrom(firstObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstObject).at(stringPath).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromInnerObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject fourthObject = new JsonObject();
		fourthObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		fourthObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonObject thirdObject = new JsonObject();
		thirdObject.add(new JsonProperty("fourthObject", fourthObject));
		
		JsonObject secondObject = new JsonObject();
		secondObject.add(new JsonProperty("thirdObject", thirdObject));
		
		JsonObject firstObject = new JsonObject();
		firstObject.add(new JsonProperty("secondObject", secondObject));
		firstObject.add(new JsonProperty("thirdObject", thirdObject));
		
		firstObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.secondObject.thirdObject"
				+ ".fourthObject.jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$.thirdObject.fourthObject.jsonString");
		
		verifyElementFrom(firstObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstObject).at(stringPath).is(jsonString);
		
		boolean removed = firstObject.removeElementAt(
				JsonPath.compile("$.thirdObject.fourthObject.jsonBoolean"));
		assertTrue(removed);
		
		verifyElementFrom(firstObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstObject).at(stringPath).is(jsonString);
	}
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromFlatSameObjectDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("innerObject1", innerObject));
		outerObject.add(new JsonProperty("innerObject2", innerObject));
		
		outerObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerObject1.jsonBoolean");
		
		verifyElementFrom(outerObject).at(path).is(jsonBoolean);
		
		boolean removed = outerObject.removeElementAt(
				JsonPath.compile("$.innerObject2.jsonBoolean"));
		assertTrue(removed);
		
		verifyElementFrom(outerObject).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(jsonBoolean);
		secondArray.add(jsonString);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("firstArray", firstArray));
		jsonObject.add(new JsonProperty("secondArray", secondArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.firstArray[0][0]");
		JsonPath stringPathBeforeRemove = JsonPath.compile("$.secondArray[1]");		
		JsonPath stringPathAfterRemove = JsonPath.compile("$.secondArray[0]");
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPathBeforeRemove).is(jsonString);
		
		boolean removed = jsonObject.removeElementAt(JsonPath.compile("$.secondArray[0]"));
		assertTrue(removed);
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPathAfterRemove).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromInnerArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(jsonBoolean);
		thirdArray.add(jsonString);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("firstArray", firstArray));
		jsonObject.add(new JsonProperty("secondArray", secondArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.firstArray[0][0][0]");
		JsonPath stringPathBeforeRemove = JsonPath.compile("$.secondArray[0][1]");
		JsonPath stringPathAfterRemove = JsonPath.compile("$.secondArray[0][0]");
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPathBeforeRemove).is(jsonString);
		
		boolean removed = jsonObject.removeElementAt(JsonPath.compile("$.secondArray[0][0]"));
		assertTrue(removed);
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPathAfterRemove).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromFlatSameArrayDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonBoolean);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray1", jsonArray));
		jsonObject.add(new JsonProperty("jsonArray2", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonArray1[0]");
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
		
		boolean removed = jsonObject.removeElementAt(JsonPath.compile("$.jsonArray2[0]"));
		assertTrue(removed);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to remove the element at must be specified")
	public void shouldThrowForRemoveElementAtNullPath() {
		new JsonObject().removeElementAt(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot remove JSON tree root")
	public void shouldThrowForRemoveElementAtRootPath() {
		new JsonObject().removeElementAt(new JsonPath());
	}
	
	@Test(expectedExceptions = IllegalStateException.class,
			expectedExceptionsMessageRegExp =
			"removeElementAt can only be called on the JSON tree root.")
	public void shouldThrowForRemoveElementAtNotTreeRoot() {
		JsonPath path = JsonPath.compile("$.jsonObject");
		new JsonObject(path).removeElementAt(path);
	}
		
	@Test
	public void shouldAddElementAtToArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", new JsonArray()));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonArray[0]");
		jsonObject.addElementAt(path, jsonBoolean);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
	}
	
	@Test
	public void shouldAddElementAtToObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonObject jsonObject = new JsonObject();
		
		JsonPath path = JsonPath.compile("$.jsonBoolean");
		jsonObject.addElementAt(path, jsonBoolean);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = {"shouldAddElementAtToArray", "shouldAddElementAtToObject"})
	public void shouldAddElementAtToNonRoot() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonObject innerObject = new JsonObject();
		
		JsonObject root = new JsonObject();
		root.add(new JsonProperty("innerObject", innerObject));
		root.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerObject.jsonBoolean");
		
		innerObject.addElementAt(path, jsonBoolean);
		
		verifyElementFrom(root).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = "shouldAddElementAtToNonRoot")
	public void shouldAddElementOnlyAtToArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray innerArray = new JsonArray();
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("outerArray", outerArray));
		jsonObject.add(new JsonProperty("innerArray", innerArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerArray[0]");
		
		boolean added = jsonObject.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
		assertFalse(jsonObject.hasElementAt(JsonPath.compile("$.outerArray[0][0]")));
	}
	
	@Test(dependsOnMethods = "shouldAddElementAtToNonRoot")
	public void shouldAddElementOnlyAtToObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject innerObject = new JsonObject();
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(innerObject);
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("jsonArray", jsonArray));
		outerObject.add(new JsonProperty("innerObject", innerObject));
		
		outerObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerObject.jsonBoolean");
		
		boolean added = outerObject.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(outerObject).at(path).is(jsonBoolean);
		assertFalse(outerObject.hasElementAt(JsonPath.compile("$.jsonArray[0].jsonBoolean")));
	}
	
	@Test
	public void shouldNotAddElementAtToPrimitive() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", new JsonBoolean(true)));
		
		jsonObject.recalculateTreePaths();
		
		boolean added = jsonObject.addElementAt(
				JsonPath.compile("$.jsonBoolean.jsonString"), new JsonString());
		assertFalse(added);
	}
	
	@Test
	public void shouldNotAddElementAtToObjectWithExistingPropertyKey() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
				
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonBoolean");
		
		boolean added = jsonObject.addElementAt(path, new JsonBoolean(false));
		assertFalse(added);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
	}
	
	@Test
	public void shouldNotAddElementAtToArrayWithInvalidIndex() {
		JsonArray jsonArray = new JsonArray();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		boolean added = jsonObject.addElementAt(
				JsonPath.compile("$.jsonArray[1]"), new JsonBoolean());
		assertFalse(added);
		
		assertTrue(jsonArray.isEmpty());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to add the element at must be specified")
	public void shouldThrowForAddElementAtNullPath() {
		new JsonObject().addElementAt(null, new JsonBoolean());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null element to be added must be specified")
	public void shouldThrowForAddElementAtNullElement() {
		new JsonObject().addElementAt(new JsonPath(), null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot add JSON tree root")
	public void shouldThrowForAddElementAtRootPath() {
		new JsonObject().addElementAt(new JsonPath(), new JsonBoolean());
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to replace the element at must be specified")
	public void shouldThrowForReplaceElementAtNullPath() {
		new JsonObject().replaceElementAt(null, new JsonBoolean());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null element to be inserted must be specified")
	public void shouldThrowForReplaceElementAtNullElement() {
		new JsonObject().replaceElementAt(new JsonPath(), null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot replace JSON tree root")
	public void shouldThrowForReplaceElementAtRootPath() {
		new JsonObject().replaceElementAt(new JsonPath(), new JsonBoolean());
	}
	
	@Test
	public void shouldReplaceElementAtInArray() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonNumber);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonPath replacePath = JsonPath.compile("$.jsonArray[0]");
		JsonPath numberPath = JsonPath.compile("$.jsonArray[1]");
		
		verifyElementFrom(jsonObject).at(numberPath).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				jsonObject.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(jsonObject).at(replacePath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(numberPath).is(jsonNumber);
		assertEquals(jsonArray.size(), 2);
	}
	
	@Test
	public void shouldReplaceElementOnlyAtInArray() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonArray innerArray = new JsonArray();
		innerArray.add(jsonString);
		innerArray.add(jsonNumber);
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("outerArray", outerArray));
		jsonObject.add(new JsonProperty("innerArray", innerArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath stringPath = JsonPath.compile("$.outerArray[0][0]");
		JsonPath replacePath = JsonPath.compile("$.innerArray[0]");
		JsonPath numberPath1 = JsonPath.compile("$.outerArray[0][1]");
		JsonPath numberPath2 = JsonPath.compile("$.innerArray[1]");
		
		verifyElementFrom(jsonObject).at(replacePath).is(jsonString);
		verifyElementFrom(jsonObject).at(stringPath).is(jsonString);
		verifyElementFrom(jsonObject).at(numberPath1).is(jsonNumber);
		verifyElementFrom(jsonObject).at(numberPath2).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				jsonObject.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(jsonObject).at(replacePath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPath).is(jsonString);
		verifyElementFrom(jsonObject).at(numberPath1).is(jsonNumber);
		verifyElementFrom(jsonObject).at(numberPath2).is(jsonNumber);
	}
	
	@Test
	public void shouldReplaceElementAtInObject() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("replaceable", jsonString));
		jsonObject.add(new JsonProperty("jsonNumber", jsonNumber));
		
		jsonObject.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath replacePath = JsonPath.compile("$.replaceable");
		JsonPath numberPath = JsonPath.compile("$.jsonNumber");
		
		verifyElementFrom(jsonObject).at(replacePath).is(jsonString);
		verifyElementFrom(jsonObject).at(numberPath).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				jsonObject.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(jsonObject).at(replacePath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(numberPath).is(jsonNumber);
		assertEquals(jsonObject.size(), 2);
	}
	
	@Test
	public void shouldReplaceElementOnlyAtInObject() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber();
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("replaceable", jsonString));
		innerObject.add(new JsonProperty("jsonNumber", jsonNumber));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(innerObject);
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("jsonArray", jsonArray));
		outerObject.add(new JsonProperty("innerObject", innerObject));
		
		outerObject.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath stringPath = JsonPath.compile("$.jsonArray[0].replaceable");
		JsonPath replacePath = JsonPath.compile("$.innerObject.replaceable");
		JsonPath numberPath1 = JsonPath.compile("$.jsonArray[0].jsonNumber");
		JsonPath numberPath2 = JsonPath.compile("$.innerObject.jsonNumber");
		
		verifyElementFrom(outerObject).at(stringPath).is(jsonString);
		verifyElementFrom(outerObject).at(replacePath).is(jsonString);
		verifyElementFrom(outerObject).at(numberPath1).is(jsonNumber);
		verifyElementFrom(outerObject).at(numberPath2).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				outerObject.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(outerObject).at(stringPath).is(jsonString);
		verifyElementFrom(outerObject).at(replacePath).is(jsonBoolean);
		verifyElementFrom(outerObject).at(numberPath1).is(jsonNumber);
		verifyElementFrom(outerObject).at(numberPath2).is(jsonNumber);
	}

	@Test
	public void shouldNotReplaceElementAtFromPrimitive() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonBoolean[0]");
		
		Optional<JsonType> optionalBeforeReplace = jsonObject.getElementAt(path);
		
		assertFalse(optionalBeforeReplace.isPresent());
		
		boolean replaced = jsonObject.replaceElementAt(path, new JsonString()).isPresent();
		
		assertFalse(replaced);
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingElement() {
		JsonArray jsonArray = new JsonArray();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.replaceElementAt(
				JsonPath.compile("$.jsonArray[0]"), new JsonBoolean()).isPresent());
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingProperty() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.replaceElementAt(
				JsonPath.compile("$.property"), new JsonBoolean()).isPresent());
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingPath() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.recalculateTreePaths();
		
		assertFalse(jsonObject.replaceElementAt(
				JsonPath.compile("$.property.property"), new JsonBoolean()).isPresent());
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInArray", "shouldAddElementAtToArray"})
	public void shouldReplaceElementOnlyAtInInnerArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(jsonBoolean);
		thirdArray.add(jsonString);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("firstArray", firstArray));
		jsonObject.add(new JsonProperty("secondArray", secondArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.firstArray[0][0][0]");
		JsonPath stringPath = JsonPath.compile("$.secondArray[0][1]");
		JsonPath replacePath = JsonPath.compile("$.secondArray[0][0]");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPath).is(jsonString);
		
		Optional<JsonType> replaced = jsonObject.replaceElementAt(
				replacePath, jsonNumber);
		
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(jsonObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(stringPath).is(jsonString);
		verifyElementFrom(jsonObject).at(replacePath).is(jsonNumber);
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInArray", "shouldAddElementAtToArray"})
	public void shouldReplaceElementOnlyAtInFlatSameArrayDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonBoolean);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonArray1", jsonArray));
		jsonObject.add(new JsonProperty("jsonArray2", jsonArray));
		
		jsonObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.jsonArray1[0]");
		JsonPath replacePath = JsonPath.compile("$.jsonArray2[0]");
		
		JsonString jsonString = new JsonString();
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
		
		Optional<JsonType> replaced = jsonObject.replaceElementAt(replacePath, jsonString);
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(jsonObject).at(path).is(jsonBoolean);
		verifyElementFrom(jsonObject).at(replacePath).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInObject", "shouldAddElementAtToObject"})
	public void shouldReplaceElementOnlyAtInInnerObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		innerObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("innerObject", innerObject));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(outerObject);
		
		JsonObject rootObject = new JsonObject();
		rootObject.add(new JsonProperty("jsonArray", jsonArray));
		rootObject.add(new JsonProperty("outerObject", outerObject));
		
		rootObject.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$.jsonArray[0].innerObject.jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$.outerObject.innerObject.jsonString");
		JsonPath replacePath = JsonPath.compile("$.outerObject.innerObject.jsonBoolean");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(rootObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(rootObject).at(stringPath).is(jsonString);
		
		Optional<JsonType> replaced = rootObject.replaceElementAt(
				replacePath, jsonNumber);
		
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(rootObject).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(rootObject).at(stringPath).is(jsonString);
		verifyElementFrom(rootObject).at(replacePath).is(jsonNumber);
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInObject", "shouldAddElementAtToObject"})
	public void shouldReplaceElementOnlyAtInFlatSameObjectDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("innerObject1", innerObject));
		outerObject.add(new JsonProperty("innerObject2", innerObject));
		
		outerObject.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$.innerObject1.jsonBoolean");
		JsonPath replacePath = JsonPath.compile("$.innerObject2.jsonBoolean");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(outerObject).at(path).is(jsonBoolean);
		
		Optional<JsonType> replaced = outerObject.replaceElementAt(replacePath, jsonNumber);
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);

		verifyElementFrom(outerObject).at(path).is(jsonBoolean);
		verifyElementFrom(outerObject).at(replacePath).is(jsonNumber);
	}

	@Test
	public void shouldNotAddTwoPropertiesWithSameKey() {
		JsonObject jsonObject = new JsonObject();
		
		assertTrue(jsonObject.add(new JsonProperty("key", "value1")));
		assertFalse(jsonObject.add(new JsonProperty("key", "value2")));
		
		assertEquals(jsonObject.size(), 1);
	}
	
	@Test
	public void shouldNotAddAllPropertiesWithSameKey() {
		JsonObject jsonObject = new JsonObject();
		
		List<JsonProperty> jsonProperties = new ArrayList<>();
		jsonProperties.add(new JsonProperty("key", "value1"));
		jsonProperties.add(new JsonProperty("key", "value2"));
		
		assertTrue(jsonObject.addAll(jsonProperties));
		assertEquals(jsonObject.size(), 1);			
	}
	
	@Test
	public void shouldPutReplaceSecondPropertyWithSameKey() {
		JsonObject jsonObject = new JsonObject();
		JsonProperties jsonProperties = jsonObject.asMap();
		
		assertNull(jsonProperties.put("key", "value1"));
		assertEquals(jsonProperties.put("key", "value2"), new JsonString("value1"));
		
		assertEquals(jsonObject.size(), 1);
	}
}

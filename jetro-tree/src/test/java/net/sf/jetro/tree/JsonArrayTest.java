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
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.JsonRenderer;

public class JsonArrayTest {
	private class JsonTypeVerifier {
		private final JsonArray jsonArray;
		private final JsonPath path;
		
		private JsonTypeVerifier(final JsonArray jsonArray, final JsonPath path) {
			Objects.requireNonNull(jsonArray);
			Objects.requireNonNull(path);
			
			this.jsonArray = jsonArray;
			this.path = path;
		}
		
		private void is(final JsonType element) {
			Optional<JsonType> optionalElement = jsonArray.getElementAt(path);
			
			assertNotNull(optionalElement);
			assertTrue(optionalElement.isPresent());
			assertTrue(optionalElement.get() == element);
		}
	}
	
	private class JsonArrayHolder {
		private final JsonArray jsonArray;
		
		private JsonArrayHolder(final JsonArray jsonArray) {
			Objects.requireNonNull(jsonArray);
			
			this.jsonArray = jsonArray;
		}
		
		private JsonTypeVerifier at(final JsonPath path) {
			return new JsonTypeVerifier(jsonArray, path);
		}
	}
	
	private JsonArrayHolder verifyElementFrom(final JsonArray jsonArray) {
		return new JsonArrayHolder(jsonArray);
	}

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
		
		verifyElementFrom(outerArray).at(path).is(jsonBoolean);
		
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
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
		
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
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(jsonBoolean);
		thirdArray.add(jsonString);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0][0]");
		JsonPath stringPathBeforeRemove = JsonPath.compile("$[1][1]");		
		JsonPath stringPathAfterRemove = JsonPath.compile("$[1][0]");
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPathBeforeRemove).is(jsonString);
		
		boolean removed = firstArray.removeElementAt(JsonPath.compile("$[1][0]"));
		assertTrue(removed);
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPathAfterRemove).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromInnerArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray fourthArray = new JsonArray();
		fourthArray.add(jsonBoolean);
		fourthArray.add(jsonString);
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(fourthArray);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0][0][0]");
		JsonPath stringPathBeforeRemove = JsonPath.compile("$[1][0][1]");
		JsonPath stringPathAfterRemove = JsonPath.compile("$[1][0][0]");
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPathBeforeRemove).is(jsonString);
		
		boolean removed = firstArray.removeElementAt(JsonPath.compile("$[1][0][0]"));
		assertTrue(removed);
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPathAfterRemove).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromArray", "shouldAddElementAtToArray"})
	public void shouldRemoveElementOnlyAtFromFlatSameArrayDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(jsonBoolean);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(secondArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0][0]");
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
		
		boolean removed = firstArray.removeElementAt(JsonPath.compile("$[1][0]"));
		assertTrue(removed);
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		jsonObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonArray innerArray = new JsonArray();
		innerArray.add(jsonObject);
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		outerArray.add(jsonObject);
		
		outerArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0].jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$[1].jsonString");
		
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
		
		boolean removed = outerArray.removeElementAt(JsonPath.compile("$[1].jsonBoolean"));
		assertTrue(removed);
		
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
	}
	
	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromInnerObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonObject innerObject = new JsonObject();
		innerObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		innerObject.add(new JsonProperty("jsonString", jsonString));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add(new JsonProperty("innerObject", innerObject));
		
		JsonArray innerArray = new JsonArray();
		innerArray.add(outerObject);
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		outerArray.add(outerObject);
		
		outerArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0].innerObject.jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$[1].innerObject.jsonString");
				
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
		
		boolean removed = outerArray.removeElementAt(
				JsonPath.compile("$[1].innerObject.jsonBoolean"));
		
		assertTrue(removed);
		
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
	}

	@Test(dependsOnMethods = {"shouldRemoveElementAtFromObject", "shouldAddElementAtToObject"})
	public void shouldRemoveElementOnlyAtFromFlatSameObjectDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean");
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
		
		boolean removed = jsonArray.removeElementAt(JsonPath.compile("$[1].jsonBoolean"));
		assertTrue(removed);

		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to remove the element at must be specified")
	public void shouldThrowForRemoveElementAtNullPath() {
		new JsonArray().removeElementAt(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot remove JSON tree root")
	public void shouldThrowForRemoveElementAtRootPath() {
		new JsonArray().removeElementAt(new JsonPath());
	}
	
	@Test(expectedExceptions = IllegalStateException.class,
			expectedExceptionsMessageRegExp =
			"removeElementAt can only be called on the JSON tree root.")
	public void shouldThrowForRemoveElementAtNotTreeRoot() {
		JsonPath path = JsonPath.compile("$.jsonArray");
		new JsonArray(path).removeElementAt(path);
	}
	
	@Test
	public void shouldAddElementAtToArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonArray jsonArray = new JsonArray();
		
		JsonPath path = JsonPath.compile("$[0]");
		boolean added = jsonArray.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
	}
	
	@Test
	public void shouldAddElementAtToObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonObject());
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean");
		boolean added = jsonArray.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = {"shouldAddElementAtToArray", "shouldAddElementAtToObject"})
	public void shouldAddElementAtToNonRoot() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonArray innerArray = new JsonArray();
		
		JsonArray root = new JsonArray();
		root.add(innerArray);
		root.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0][0]");
		
		boolean added = innerArray.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(root).at(path).is(jsonBoolean);
	}
	
	@Test(dependsOnMethods = "shouldAddElementAtToNonRoot")
	public void shouldAddElementOnlyAtToArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray thirdArray = new JsonArray();
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[1][0]");
		
		boolean added = firstArray.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
		assertFalse(firstArray.hasElementAt(JsonPath.compile("$[0][0][0]")));
	}
	
	@Test(dependsOnMethods = "shouldAddElementAtToNonRoot")
	public void shouldAddElementOnlyAtToObject() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(jsonObject);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(jsonObject);
		
		firstArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[1].jsonBoolean");
		
		boolean added = firstArray.addElementAt(path, jsonBoolean);
		assertTrue(added);
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
		assertFalse(firstArray.hasElementAt(JsonPath.compile("$[0][0].jsonBoolean")));
	}
	
	@Test
	public void shouldNotAddElementAtToPrimitive() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonBoolean(true));
		
		jsonArray.recalculateTreePaths();
		
		boolean added = jsonArray.addElementAt(JsonPath.compile("$[0][0]"), new JsonString());
		assertFalse(added);
	}
	
	@Test
	public void shouldNotAddElementAtToObjectWithExistingPropertyKey() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean");
		
		boolean added = jsonArray.addElementAt(path, new JsonBoolean(false));
		assertFalse(added);
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
	}
	
	@Test
	public void shouldNotAddElementAtToArrayWithInvalidIndex() {
		JsonArray jsonArray = new JsonArray();
		
		boolean added = jsonArray.addElementAt(JsonPath.compile("$[1]"), new JsonBoolean());
		assertFalse(added);
		
		assertTrue(jsonArray.isEmpty());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to add the element at must be specified")
	public void shouldThrowForAddElementAtNullPath() {
		new JsonArray().addElementAt(null, new JsonBoolean());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null element to be added must be specified")
	public void shouldThrowForAddElementAtNullElement() {
		new JsonArray().addElementAt(new JsonPath(), (JsonType) null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot add JSON tree root")
	public void shouldThrowForAddElementAtRootPath() {
		new JsonArray().addElementAt(new JsonPath(), new JsonBoolean());
	}
	
	@Test
	public void shouldHaveElementAt() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonBoolean(true));
		
		jsonArray.recalculateTreePaths();
		
		boolean hasElement = jsonArray.hasElementAt(JsonPath.compile("$[0]"));
		assertTrue(hasElement);
	}
	
	@Test
	public void shouldNotHaveElementAt() {
		JsonArray jsonArray = new JsonArray();
		
		boolean hasElement = jsonArray.hasElementAt(JsonPath.compile("$[0]"));
		assertFalse(hasElement);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null path to replace the element at must be specified")
	public void shouldThrowForReplaceElementAtNullPath() {
		new JsonArray().replaceElementAt(null, new JsonBoolean());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp =
			"A non-null element to be inserted must be specified")
	public void shouldThrowForReplaceElementAtNullElement() {
		new JsonArray().replaceElementAt(new JsonPath(), (JsonType) null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Cannot replace JSON tree root")
	public void shouldThrowForReplaceElementAtRootPath() {
		new JsonArray().replaceElementAt(new JsonPath(), new JsonBoolean());
	}
	
	@Test
	public void shouldReplaceElementAtInArray() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonString);
		jsonArray.add(jsonNumber);
		
		jsonArray.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonPath replacePath = JsonPath.compile("$[0]");
		JsonPath numberPath = JsonPath.compile("$[1]");
		
		verifyElementFrom(jsonArray).at(numberPath).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				jsonArray.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(jsonArray).at(replacePath).is(jsonBoolean);
		verifyElementFrom(jsonArray).at(numberPath).is(jsonNumber);
		assertEquals(jsonArray.size(), 2);
	}
	
	@Test
	public void shouldReplaceElementOnlyAtInArray() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(jsonString);
		thirdArray.add(jsonNumber);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath stringPath = JsonPath.compile("$[0][0][0]");
		JsonPath replacePath = JsonPath.compile("$[1][0]");
		JsonPath numberPath1 = JsonPath.compile("$[0][0][1]");
		JsonPath numberPath2 = JsonPath.compile("$[1][1]");
		
		verifyElementFrom(firstArray).at(replacePath).is(jsonString);
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		verifyElementFrom(firstArray).at(numberPath1).is(jsonNumber);
		verifyElementFrom(firstArray).at(numberPath2).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				firstArray.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(firstArray).at(replacePath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		verifyElementFrom(firstArray).at(numberPath1).is(jsonNumber);
		verifyElementFrom(firstArray).at(numberPath2).is(jsonNumber);
	}
	
	@Test
	public void shouldReplaceElementAtInObject() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber(1);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("replaceable", jsonString));
		jsonObject.add(new JsonProperty("jsonNumber", jsonNumber));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath replacePath = JsonPath.compile("$[0].replaceable");
		JsonPath numberPath = JsonPath.compile("$[0].jsonNumber");
		
		verifyElementFrom(jsonArray).at(replacePath).is(jsonString);
		verifyElementFrom(jsonArray).at(numberPath).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				jsonArray.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(jsonArray).at(replacePath).is(jsonBoolean);
		verifyElementFrom(jsonArray).at(numberPath).is(jsonNumber);
		assertEquals(jsonObject.size(), 2);
	}
	
	@Test
	public void shouldReplaceElementOnlyAtInObject() {
		JsonString jsonString = new JsonString();
		JsonNumber jsonNumber = new JsonNumber();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("replaceable", jsonString));
		jsonObject.add(new JsonProperty("jsonNumber", jsonNumber));
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(jsonObject);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(jsonObject);
		
		firstArray.recalculateTreePaths();
		
		JsonBoolean jsonBoolean = new JsonBoolean();
		
		JsonPath stringPath = JsonPath.compile("$[0][0].replaceable");
		JsonPath replacePath = JsonPath.compile("$[1].replaceable");
		JsonPath numberPath1 = JsonPath.compile("$[0][0].jsonNumber");
		JsonPath numberPath2 = JsonPath.compile("$[1].jsonNumber");
		
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		verifyElementFrom(firstArray).at(replacePath).is(jsonString);
		verifyElementFrom(firstArray).at(numberPath1).is(jsonNumber);
		verifyElementFrom(firstArray).at(numberPath2).is(jsonNumber);
		
		Optional<JsonType> replacedElement =
				firstArray.replaceElementAt(replacePath, jsonBoolean);
		
		assertNotNull(replacedElement);
		assertTrue(replacedElement.isPresent());
		assertTrue(replacedElement.get() == jsonString);
		
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		verifyElementFrom(firstArray).at(replacePath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(numberPath1).is(jsonNumber);
		verifyElementFrom(firstArray).at(numberPath2).is(jsonNumber);
	}

	@Test
	public void shouldNotReplaceElementAtFromPrimitive() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean[0]");
		
		Optional<JsonType> optionalBeforeReplace = jsonArray.getElementAt(path);
		
		assertFalse(optionalBeforeReplace.isPresent());
		
		boolean replaced = jsonArray.replaceElementAt(path, new JsonString()).isPresent();
		
		assertFalse(replaced);
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingElement() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.replaceElementAt(
				JsonPath.compile("$[0]"), new JsonBoolean()).isPresent());
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingProperty() {
		JsonObject jsonObject = new JsonObject();
		
		JsonArray jsonArray = new JsonArray(Arrays.asList(jsonObject));
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.replaceElementAt(
				JsonPath.compile("$[0].property"), new JsonBoolean()).isPresent());
	}
	
	@Test
	public void shouldNotReplaceElementAtWithNonexistingPath() {
		JsonArray jsonArray = new JsonArray();
		jsonArray.recalculateTreePaths();
		
		assertFalse(jsonArray.replaceElementAt(
				JsonPath.compile("$[0][0]"), new JsonBoolean()).isPresent());
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInArray", "shouldAddElementAtToArray"})
	public void shouldReplaceElementOnlyAtInInnerArray() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		JsonString jsonString = new JsonString("jsonString");
		
		JsonArray fourthArray = new JsonArray();
		fourthArray.add(jsonBoolean);
		fourthArray.add(jsonString);
		
		JsonArray thirdArray = new JsonArray();
		thirdArray.add(fourthArray);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(thirdArray);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(thirdArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0][0][0]");
		JsonPath stringPath = JsonPath.compile("$[1][0][1]");
		JsonPath replacePath = JsonPath.compile("$[1][0][0]");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		
		Optional<JsonType> replaced = firstArray.replaceElementAt(
				replacePath, jsonNumber);
		
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(firstArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(firstArray).at(stringPath).is(jsonString);
		verifyElementFrom(firstArray).at(replacePath).is(jsonNumber);
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInArray", "shouldAddElementAtToArray"})
	public void shouldReplaceElementOnlyAtInFlatSameArrayDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonArray secondArray = new JsonArray();
		secondArray.add(jsonBoolean);
		
		JsonArray firstArray = new JsonArray();
		firstArray.add(secondArray);
		firstArray.add(secondArray);
		
		firstArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0][0]");
		JsonPath replacePath = JsonPath.compile("$[1][0]");
		
		JsonString jsonString = new JsonString();
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
		
		Optional<JsonType> replaced = firstArray.replaceElementAt(replacePath, jsonString);
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(firstArray).at(path).is(jsonBoolean);
		verifyElementFrom(firstArray).at(replacePath).is(jsonString);
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
		
		JsonArray innerArray = new JsonArray();
		innerArray.add(outerObject);
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray);
		outerArray.add(outerObject);
		
		outerArray.recalculateTreePaths();
		
		JsonPath booleanPath = JsonPath.compile("$[0][0].innerObject.jsonBoolean");
		JsonPath stringPath = JsonPath.compile("$[1].innerObject.jsonString");
		JsonPath replacePath = JsonPath.compile("$[1].innerObject.jsonBoolean");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
		
		Optional<JsonType> replaced = outerArray.replaceElementAt(
				replacePath, jsonNumber);
		
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);
		
		verifyElementFrom(outerArray).at(booleanPath).is(jsonBoolean);
		verifyElementFrom(outerArray).at(stringPath).is(jsonString);
		verifyElementFrom(outerArray).at(replacePath).is(jsonNumber);
	}

	@Test(dependsOnMethods = {"shouldReplaceElementAtInObject", "shouldAddElementAtToObject"})
	public void shouldReplaceElementOnlyAtInFlatSameObjectDuplicate() {
		JsonBoolean jsonBoolean = new JsonBoolean(true);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(new JsonProperty("jsonBoolean", jsonBoolean));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(jsonObject);
		jsonArray.add(jsonObject);
		
		jsonArray.recalculateTreePaths();
		
		JsonPath path = JsonPath.compile("$[0].jsonBoolean");
		JsonPath replacePath = JsonPath.compile("$[1].jsonBoolean");
		
		JsonNumber jsonNumber = new JsonNumber();
		
		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
		
		Optional<JsonType> replaced = jsonArray.replaceElementAt(replacePath, jsonNumber);
		assertTrue(replaced.isPresent());
		assertTrue(replaced.get() == jsonBoolean);

		verifyElementFrom(jsonArray).at(path).is(jsonBoolean);
		verifyElementFrom(jsonArray).at(replacePath).is(jsonNumber);
	}
}

/*
 * #%L
 * Jetro Patch
 * %%
 * Copyright (C) 2013 - 2019 The original author or authors.
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
package net.sf.jetro.patch;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.visitor.JsonVisitor;

public class AddPatchOperationTest {
	
	@Test
	public void shouldApplyPatchOnObject() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/foo"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		JsonType actual = operation.applyPatch(new JsonObject());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("foo", "bar"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldApplyPatchOnArray() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/0"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		JsonType actual = operation.applyPatch(new JsonArray());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonString("bar"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldApplyPatchOnArrayLastElement() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/-"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		JsonArray jsonArray = new JsonArray();
		jsonArray.add(new JsonString("foo"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		JsonType actual = operation.applyPatch(jsonArray);
		
		JsonArray expected = jsonArray.deepCopy();
		expected.add(new JsonString("bar"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldApplyPatchOnRootPath() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", ""));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		JsonType actual = operation.applyPatch(new JsonObject());
		
		JsonString expected = new JsonString("bar");
		
		assertEquals(actual, expected);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "Argument 'patchDefinition' must not be null")
	public void shouldNotApplyPatchNullPatchDefinition() {
		new AddPatchOperation(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Missing property 'path'")
	public void shouldNotApplyPatchMissingPropertyPathOnPatchDefinition() {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		new AddPatchOperation(patchDefinition);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "Missing property 'value'")
	public void shouldNotApplyPatchMissingPropertyValueOnPatchDefinition() {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", ""));
		
		new AddPatchOperation(patchDefinition);
	}
	
	@Test(expectedExceptions = JsonPatchException.class,
			expectedExceptionsMessageRegExp = "Couldn't add JsonString \\[value=bar, "
					+ "paths=\\[\\$]] to JsonArray \\[values=\\[], paths=\\[\\$]] at path "
					+ "\"/foo\"")
	public void shouldNotApplyPatchObjectPathOnArray() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/foo"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		operation.applyPatch(new JsonArray());
	}
	
	@Test(expectedExceptions = JsonPatchException.class,
			expectedExceptionsMessageRegExp = "Couldn't add JsonString \\[value=bar, "
					+ "paths=\\[\\$]] to JsonObject \\[properties=\\[], paths=\\[\\$]] "
					+ "at path \"/0\"")
	public void shouldNotApplyPatchArrayPathOnObject() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/0"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		operation.applyPatch(new JsonObject());
	}
	
	@Test(expectedExceptions = JsonPatchException.class,
			expectedExceptionsMessageRegExp = "Expected JsonArray at \"\"")
	public void shouldNotApplyPatchNextToLastArrayPathOnObject() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/-"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		operation.applyPatch(new JsonObject());
	}
	
	@Test(expectedExceptions = JsonPatchException.class,
			expectedExceptionsMessageRegExp = "java\\.lang\\.IllegalArgumentException: source "
					+ "must either be a JsonArray or a JsonObject")
	public void shouldNotApplyPatchNextToLastArrayPathOnPrimitive() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/-"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		operation.applyPatch(new JsonString());
	}
	
	@SuppressWarnings("serial")
	@Test(expectedExceptions = JsonPatchException.class,
			expectedExceptionsMessageRegExp = "Couldn't add JsonString \\[value=bar, "
					+ "paths=\\[\\$]] to net\\.sf\\.jetro\\.patch\\.AddPatchOperationTest\\$1@"
					+ "[0-9a-f]{8} at path \"/foo\"\\. The parent value at \"\" is neither "
					+ "a JsonObject nor a JsonArray")
	public void shouldNotApplyPatchWrongJsonCollection() throws JsonPatchException {
		JsonObject patchDefinition = new JsonObject();
		patchDefinition.add(new JsonProperty("path", "/foo"));
		patchDefinition.add(new JsonProperty("value", "bar"));
		
		AddPatchOperation operation = new AddPatchOperation(patchDefinition);
		operation.applyPatch(new JsonCollection() {

			@Override
			public JsonType deepCopy() {
				return this;
			}

			@Override
			public Optional<JsonType> getElementAt(JsonPath path) {
				return Optional.of(this);
			}

			@Override
			public void addPath(JsonPath path) {
			}

			@Override
			public String toJson() {
				return "";
			}

			@Override
			public String toJson(JsonRenderer renderer) {
				return renderer.render(this);
			}

			@Override
			public void mergeInto(JsonVisitor<?> visitor) {
			}

			@Override
			public void recalculateTreePaths(boolean treeRoot) {
			}

			@Override
			public void resetPathsRecursively() {
			}

			@Override
			public boolean addElementAt(JsonPath path, JsonType element) {
				return false;
			}

			@Override
			public Optional<JsonType> replaceElementAt(JsonPath path, JsonType newElement) {
				return Optional.empty();
			}

			@Override
			public boolean removeElementAt(JsonPath path) {
				return false;
			}
		});
	}
}

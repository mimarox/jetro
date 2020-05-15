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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.patch.data.AddPatchOperationData;
import net.sf.jetro.patch.data.CopyPatchOperationData;
import net.sf.jetro.patch.data.MovePatchOperationData;
import net.sf.jetro.patch.data.PatchOperationData;
import net.sf.jetro.patch.data.RemovePatchOperationData;
import net.sf.jetro.patch.data.ReplacePatchOperationData;
import net.sf.jetro.patch.data.TestPatchOperationData;
import net.sf.jetro.patch.pointer.JsonPointer;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;

@Test(groups = "individualTests")
public class JsonPatchOperationsCollectorTest {
	private static final JsonObject SOURCE = new JsonObject();
	
	private JsonPatchOperationsCollector getJsonSourceCollector() {
		return new JsonPatchOperationsCollector(SOURCE);
	}
	
	@Test
	public void testApplyingString() {
		JsonPatchApplier applier = getJsonSourceCollector().applying("[]");

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray());
	}
	
	@Test
	public void testApplyingInputStream() throws IOException {
		JsonPatchApplier applier = getJsonSourceCollector().applying(
				new ByteArrayInputStream("[]".getBytes("UTF-8")));
		
		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray());
	}
	
	@Test
	public void testApplyingReader() throws IOException {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new StringReader("[]"));

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray());
	}
	
	@Test
	public void testApplyingJsonArray() {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new JsonArray());

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray());
	}
	
	@Test
	public void testApplyingJsonObject() {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new JsonObject());
		
		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray(
				Arrays.asList(new JsonObject())));
	}
	
	@Test
	public void testApplyingPatchOperationData() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		RemovePatchOperationData patchOperation = new RemovePatchOperationData(path);
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperation);
		
		JsonObject expectedOperation = new JsonObject();
		expectedOperation.add(new JsonProperty("op", "remove"));
		expectedOperation.add(new JsonProperty("path", path.toString()));
		
		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray(
				Arrays.asList(expectedOperation)));
	}
	
	@Test
	public void testApplyingListOfPatchOperationData() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		JsonPointer from = JsonPointer.compile("/a/b/2");
		Object value = new ArrayList<>();
		
		List<PatchOperationData> patchOperations = new ArrayList<>();
		patchOperations.add(new RemovePatchOperationData(path));
		patchOperations.add(new AddPatchOperationData(path, value));
		patchOperations.add(new ReplacePatchOperationData(path, value));
		patchOperations.add(new TestPatchOperationData(path, value));
		patchOperations.add(new MovePatchOperationData(path, from));
		patchOperations.add(new CopyPatchOperationData(path, from));
		
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperations);
		
		JsonProperty pathProperty = new JsonProperty("path", path.toString());
		JsonProperty fromProperty = new JsonProperty("from", from.toString());
		JsonProperty valueProperty = new JsonProperty("value", new JsonArray());
		
		JsonObject removeOperation = new JsonObject();
		removeOperation.add(new JsonProperty("op", "remove"));
		removeOperation.add(pathProperty);
		
		JsonObject addOperation = new JsonObject();
		addOperation.add(new JsonProperty("op", "add"));
		addOperation.add(pathProperty);
		addOperation.add(valueProperty);
		
		JsonObject replaceOperation = new JsonObject();
		replaceOperation.add(new JsonProperty("op", "replace"));
		replaceOperation.add(pathProperty);
		replaceOperation.add(valueProperty);

		JsonObject testOperation = new JsonObject();
		testOperation.add(new JsonProperty("op", "test"));
		testOperation.add(pathProperty);
		testOperation.add(valueProperty);
		
		JsonObject moveOperation = new JsonObject();
		moveOperation.add(new JsonProperty("op", "move"));
		moveOperation.add(pathProperty);
		moveOperation.add(fromProperty);

		JsonObject copyOperation = new JsonObject();
		copyOperation.add(new JsonProperty("op", "copy"));
		copyOperation.add(pathProperty);
		copyOperation.add(fromProperty);

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray(Arrays.asList(
				removeOperation, addOperation, replaceOperation, testOperation,
				moveOperation, copyOperation)));
	}
	
	@Test
	public void testApplyingPatchOperationDataWithContext() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		RemovePatchOperationData patchOperation = new RemovePatchOperationData(path);
		
		SerializationContext context = new SerializationContext();
		
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperation, context);
		
		JsonObject expectedOperation = new JsonObject();
		expectedOperation.add(new JsonProperty("op", "remove"));
		expectedOperation.add(new JsonProperty("path", path.toString()));

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray(
				Arrays.asList(expectedOperation)));
	}
	
	@Test
	public void testApplyingListOfPatchOperationDataWithContext() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		JsonPointer from = JsonPointer.compile("/a/b/2");
		Object value = new ArrayList<>();
		
		List<PatchOperationData> patchOperations = new ArrayList<>();
		patchOperations.add(new RemovePatchOperationData(path));
		patchOperations.add(new AddPatchOperationData(path, value));
		patchOperations.add(new ReplacePatchOperationData(path, value));
		patchOperations.add(new TestPatchOperationData(path, value));
		patchOperations.add(new MovePatchOperationData(path, from));
		patchOperations.add(new CopyPatchOperationData(path, from));
		
		SerializationContext context = new SerializationContext();
		
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperations, context);
		
		JsonProperty pathProperty = new JsonProperty("path", path.toString());
		JsonProperty fromProperty = new JsonProperty("from", from.toString());
		JsonProperty valueProperty = new JsonProperty("value", new JsonArray());
		
		JsonObject removeOperation = new JsonObject();
		removeOperation.add(new JsonProperty("op", "remove"));
		removeOperation.add(pathProperty);
		
		JsonObject addOperation = new JsonObject();
		addOperation.add(new JsonProperty("op", "add"));
		addOperation.add(pathProperty);
		addOperation.add(valueProperty);
		
		JsonObject replaceOperation = new JsonObject();
		replaceOperation.add(new JsonProperty("op", "replace"));
		replaceOperation.add(pathProperty);
		replaceOperation.add(valueProperty);

		JsonObject testOperation = new JsonObject();
		testOperation.add(new JsonProperty("op", "test"));
		testOperation.add(pathProperty);
		testOperation.add(valueProperty);
		
		JsonObject moveOperation = new JsonObject();
		moveOperation.add(new JsonProperty("op", "move"));
		moveOperation.add(pathProperty);
		moveOperation.add(fromProperty);

		JsonObject copyOperation = new JsonObject();
		copyOperation.add(new JsonProperty("op", "copy"));
		copyOperation.add(pathProperty);
		copyOperation.add(fromProperty);

		assertNotNull(applier);
		assertTrue(applier.getSource() == SOURCE);
		assertEquals(applier.getPatchOperations(), new JsonArray(Arrays.asList(
				removeOperation, addOperation, replaceOperation, testOperation,
				moveOperation, copyOperation)));
	}
}

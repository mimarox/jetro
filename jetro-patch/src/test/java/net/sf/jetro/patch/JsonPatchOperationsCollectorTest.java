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

import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

@Test(groups = "individualTests")
public class JsonSourceCollectorTest {
	private JsonSourceCollector getJsonSourceCollector() {
		return new JsonSourceCollector(new JsonObject());
	}
	
	@Test
	public void testApplyingString() {
		JsonPatchApplier applier = getJsonSourceCollector().applying("[]");
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingInputStream() throws IOException {
		JsonPatchApplier applier = getJsonSourceCollector().applying(
				new ByteArrayInputStream("[]".getBytes("UTF-8")));
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingReader() {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new StringReader("[]"));
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingJsonArray() {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new JsonArray());
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingJsonObject() {
		JsonPatchApplier applier = getJsonSourceCollector().applying(new JsonObject());
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingPatchOperationData() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		RemovePatchOperationData patchOperation = new RemovePatchOperationData(path);
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperation);
		
		assertNotNull(applier);
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
		
		assertNotNull(applier);
	}
	
	@Test
	public void testApplyingPatchOperationDataWithContext() {
		JsonPointer path = JsonPointer.compile("/a/b/1");
		RemovePatchOperationData patchOperation = new RemovePatchOperationData(path);
		
		SerializationContext context = new SerializationContext();
		
		JsonPatchApplier applier = getJsonSourceCollector().applying(patchOperation, context);
		
		assertNotNull(applier);
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
		
		assertNotNull(applier);
	}
}

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
package net.sf.jetro.patch.data;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.addons.ToStringSerializer;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.patch.pointer.JsonPointer;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;

public class PatchOperationDataTest {

	@Test
	public void shouldConvertToJsonType() {
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
		
		JsonArray jsonValue = new JsonArray();
		
		JsonArray expected = new JsonArray();
		expected.add(createPatchOperationObject("remove", path.toString()));
		expected.add(createPatchOperationObject("add", path.toString(), jsonValue));
		expected.add(createPatchOperationObject("replace", path.toString(), jsonValue));
		expected.add(createPatchOperationObject("test", path.toString(), jsonValue));
		expected.add(createPatchOperationObject("move", path.toString(), from.toString()));
		expected.add(createPatchOperationObject("copy", path.toString(), from.toString()));
		
		SerializationContext context = new SerializationContext();
		context.addTypeSerializer(new ToStringSerializer(JsonPointer.class));
		
		ObjectVisitingReader reader = new ObjectVisitingReader(patchOperations, context);
		JsonTreeBuildingVisitor visitor = new JsonTreeBuildingVisitor();
		
		reader.accept(visitor);
		
		JsonElement actual = visitor.getVisitingResult();
		
		assertEquals(actual, expected);		
	}

	private JsonObject createPatchOperationObject(final String op, final String path) {
		JsonObject object = new JsonObject();
		object.add(new JsonProperty("op", op));
		object.add(new JsonProperty("path", path));
		
		return object;
	}

	private JsonObject createPatchOperationObject(final String op, final String path,
			final JsonType value) {
		JsonObject object = createPatchOperationObject(op, path);
		object.add(new JsonProperty("value", value));
		
		return object;
	}

	private JsonObject createPatchOperationObject(final String op, final String path,
			final String from) {
		JsonObject object = createPatchOperationObject(op, path);
		object.add(new JsonProperty("from", from));
		
		return object;
	}
}

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.stream.visitor.JsonWritingVisitor;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;

public class JsonPatchApplier {
	private static DeserializationContext deserializationContext;
	
	private final JsonType source;
	private final JsonArray patchOperations;
	
	private static DeserializationContext getDeserializationContext() {
		if (deserializationContext == null) {
			deserializationContext = DeserializationContext.getDefault();
		}
		
		return deserializationContext;
	}
	
	JsonPatchApplier(final JsonType source, final JsonCollection patchOperations) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		
		this.source = source;
		
		if (patchOperations instanceof JsonArray) {
			this.patchOperations = (JsonArray) patchOperations;
		} else {
			this.patchOperations = new JsonArray();
			this.patchOperations.add(patchOperations);
		}
	}
	
	JsonType getSource() {
		return source;
	}

	JsonArray getPatchOperations() {
		return patchOperations;
	}

	public String andReturnAsJson() throws JsonPatchException {
		return createTarget().toJson();
	}
	
	public String andReturnAsJson(final JsonRenderer renderer) throws JsonPatchException {
		Objects.requireNonNull(renderer, "Argument 'renderer' must not be null");
		return createTarget().toJson(renderer);
	}
	
	public void andWriteResultTo(final OutputStream out) throws JsonPatchException {
		Objects.requireNonNull(out, "Argument 'out' must not be null");
		
		try (OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8")) {
			andWriteResultTo(writer);
		} catch (IOException e) {
			//should never happen
			throw new RuntimeException(e);
		}
	}
	
	public void andWriteResultTo(final Writer out) throws JsonPatchException {
		Objects.requireNonNull(out, "Argument 'out' must not be null");
		
		JsonType target = createTarget();

		JsonElementVisitingReader reader = new JsonElementVisitingReader(target);
		JsonWritingVisitor visitor = new JsonWritingVisitor(new JsonWriter(out));
		
		reader.accept(visitor);
	}
	
	public JsonType andReturnAsJsonType() throws JsonPatchException {
		return createTarget();
	}
	
	public <T> T andReturnAsObject(final Class<T> clazz) throws JsonPatchException {
		Objects.requireNonNull(clazz, "Argument 'clazz' must not be null");
		
		return andReturnAsObject(TypeToken.of(clazz));
	}
	
	public <T> T andReturnAsObject(final TypeToken<T> typeToken) throws JsonPatchException {
		Objects.requireNonNull(typeToken, "Argument 'typeToken' must not be null");
		
		return andReturnAsObject(typeToken, getDeserializationContext());
	}
	
	public <T> T andReturnAsObject(final Class<T> clazz, final DeserializationContext context)
			throws JsonPatchException {
		Objects.requireNonNull(clazz, "Argument 'clazz' must not be null");
		Objects.requireNonNull(context, "Argument 'context' must not be null");
		
		return andReturnAsObject(TypeToken.of(clazz), context);
	}
	
	public <T> T andReturnAsObject(final TypeToken<T> typeToken,
			final DeserializationContext context) throws JsonPatchException {
		Objects.requireNonNull(typeToken, "Argument 'typeToken' must not be null");
		Objects.requireNonNull(context, "Argument 'context' must not be null");

		JsonType target = createTarget();
		
		JsonElementVisitingReader reader = new JsonElementVisitingReader(target);
		ObjectBuildingVisitor<T> visitor = new ObjectBuildingVisitor<>(typeToken, context);
		
		reader.accept(visitor);
		
		return visitor.getVisitingResult();
	}
	
	private JsonType createTarget() throws JsonPatchException {
		try {
			return createPatchOperations().applyPatch(source);
		} catch (JsonPatchException e) {
			throw e;
		} catch (Exception e) {
			throw new JsonPatchException("Exception while preparing or "
					+ "executing patch operations", e);
		}
	}
	
	private JsonPatchOperation createPatchOperations() {
		final List<JsonPatchOperation> patchOperationsList = createPatchOperationsList();
		
		if (patchOperationsList.size() > 0) {
			return mergePatchOperations(patchOperationsList);
		} else {
			throw new IllegalArgumentException("The JSON structure [" + patchOperations
					+ "] doesn't contain any valid patch operations");
		}
	}

	private List<JsonPatchOperation> createPatchOperationsList() {
		final List<JsonPatchOperation> patchOperationsList = new ArrayList<>();
		
		patchOperations.stream().map(this::toJsonObject).forEach(patchDefinition -> {
			patchOperationsList.add(createPatchOperation(patchDefinition));
		});
		
		return patchOperationsList;
	}

	private JsonObject toJsonObject(final JsonType element) {
		if (element instanceof JsonObject) {
			return (JsonObject) element;
		} else {
			throw new IllegalArgumentException("Expected JsonObject, but found "
					+ element.getClass().getSimpleName());
		}
	}

	private JsonPatchOperation createPatchOperation(JsonObject patchDefinition) {
		if (!patchDefinition.containsAllKeys(Arrays.asList("op", "path"))) {
			throw new IllegalArgumentException("At least one of the following properties is "
					+ "missing: 'op', 'path'");
		}
		
		JsonPatchOperation patchOperation;
		final String op = ((JsonString) patchDefinition.get("op")).getValue();

		switch (op) {
			case "add":
				patchOperation = new AddPatchOperation(patchDefinition);
				break;
			case "remove":
				patchOperation = new RemovePatchOperation(patchDefinition);
				break;
			case "replace":
				patchOperation = new ReplacePatchOperation(patchDefinition);
				break;
			case "move":
				patchOperation = new MovePatchOperation(patchDefinition);
				break;
			case "copy":
				patchOperation = new CopyPatchOperation(patchDefinition);
				break;
			case "test":
				patchOperation = new TestPatchOperation(patchDefinition);
				break;
			default:
				throw new IllegalArgumentException("Unsupported op '" + op + "'");
		}
		
		return patchOperation;
	}

	private JsonPatchOperation mergePatchOperations(
			final List<JsonPatchOperation> patchOperationsList) {
		for (int i = 1; i < patchOperationsList.size(); i++) {
			patchOperationsList.get(0).attachOperation(patchOperationsList.get(i));
		}
		
		return patchOperationsList.get(0);
	}
}

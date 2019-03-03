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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.object.serializer.addons.ToStringSerializer;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.patch.data.PatchOperationData;
import net.sf.jetro.patch.pointer.JsonPointer;
import net.sf.jetro.tree.JsonCollection;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.builder.JsonTreeBuilder;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;

public class JsonSourceCollector {
	private static final JsonTreeBuilder BUILDER = new JsonTreeBuilder();
	
	private final JsonType source;
	
	JsonSourceCollector(final JsonType source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		this.source = source;
	}

	public JsonPatchApplier applying(final String patchOperations) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		
		JsonElement jsonElement = BUILDER.build(patchOperations);
		return handleJsonElement(jsonElement);
	}

	public JsonPatchApplier applying(final InputStream patchOperations) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		
		try {
			JsonElement jsonElement = BUILDER.build(
					new InputStreamReader(patchOperations, "UTF-8"));
			return handleJsonElement(jsonElement);
		} catch (UnsupportedEncodingException e) {
			//should never happen
			throw new RuntimeException(e);
		}
	}

	public JsonPatchApplier applying(final Reader patchOperations) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		
		JsonElement jsonElement = BUILDER.build(patchOperations);
		return handleJsonElement(jsonElement);
	}

	public JsonPatchApplier applying(final JsonCollection patchOperations) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		return new JsonPatchApplier(source, patchOperations);
	}

	public JsonPatchApplier applying(final List<PatchOperationData> patchOperations) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		return applying((Object) patchOperations);
	}

	public JsonPatchApplier applying(final PatchOperationData patchOperation) {
		Objects.requireNonNull(patchOperation, "Argument 'patchOperation' must not be null");
		return applying((Object) patchOperation);
	}

	public JsonPatchApplier applying(final List<PatchOperationData> patchOperations,
			final SerializationContext context) {
		Objects.requireNonNull(patchOperations, "Argument 'patchOperations' must not be null");
		Objects.requireNonNull(context, "Argument 'context' must not be null");
		
		return applying((Object) patchOperations, context);
	}

	public JsonPatchApplier applying(final PatchOperationData patchOperation,
			final SerializationContext context) {
		Objects.requireNonNull(patchOperation, "Argument 'patchOperation' must not be null");
		Objects.requireNonNull(context, "Argument 'context' must not be null");
		
		return applying((Object) patchOperation, context);
	}

	private JsonPatchApplier applying(final Object patchOperations) {
		return applying(patchOperations, new SerializationContext());
	}

	private JsonPatchApplier applying(final Object patchOperations,
			final SerializationContext context) {
		addJsonPointerSerializerIfNecessary(context);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(patchOperations, context);
		JsonTreeBuildingVisitor visitor = new JsonTreeBuildingVisitor();
		
		reader.accept(visitor);
		
		return handleJsonElement(visitor.getVisitingResult());
	}

	private void addJsonPointerSerializerIfNecessary(final SerializationContext context) {
		boolean addingNecessary = false;
		
		try {
			TypeSerializer<Object> serializer = context.getTypeSerializer(new JsonPointer());
			
			if (!(serializer instanceof ToStringSerializer)) {
				addingNecessary = true;
			}
		} catch (IllegalStateException e) {
			addingNecessary = true;
		}
		
		if (addingNecessary) {
			context.addTypeSerializer(new ToStringSerializer(JsonPointer.class));
		}
	}
	
	private JsonPatchApplier handleJsonElement(JsonElement jsonElement) {
		if (jsonElement instanceof JsonCollection) {
			return new JsonPatchApplier(source, (JsonCollection) jsonElement);
		} else {
			throw new IllegalArgumentException("Argument 'patchOperations' must be "
					+ "convertible to a valid JsonCollection");
		}
	}
}

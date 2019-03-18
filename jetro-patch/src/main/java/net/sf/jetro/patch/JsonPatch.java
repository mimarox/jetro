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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.builder.JsonTreeBuilder;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;

/**
 * This class is the entry point to Jetro's
 * <a href="https://tools.ietf.org/html/rfc6902" target="_blank">RFC 6902</a>
 * JSON Patch implementation. It provides several static <code>patch</code> methods that
 * act as entry points to a fluent API. To actually create a patched JSON document a call
 * like <code>JsonPatch.patch(source).applying(patchOperations).andReturnAsJson()</code>
 * needs to be made. All three method parts of the call come in different variations and
 * can be combined flexibly.
 */
public class JsonPatch {
	private static final JsonTreeBuilder BUILDER = new JsonTreeBuilder(true);
	private static SerializationContext serializationContext;
	
	private JsonPatch() {}
	
	public static JsonPatchOperationsCollector patch(final String source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");

		JsonElement jsonElement = BUILDER.build(source);
		return handleJsonElement(jsonElement);
	}
	
	public static JsonPatchOperationsCollector patch(final InputStream source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		try (InputStreamReader reader = new InputStreamReader(source, "UTF-8")) {
			JsonElement jsonElement = BUILDER.build(reader);
			return handleJsonElement(jsonElement);
		} catch (IOException e) {
			//should never happen
			throw new RuntimeException(e);
		}
	}

	public static JsonPatchOperationsCollector patch(final Reader source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		JsonElement jsonElement = BUILDER.build(source);
		return handleJsonElement(jsonElement);
	}

	public static JsonPatchOperationsCollector patch(final JsonType source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		return new JsonPatchOperationsCollector(source);
	}

	public static JsonPatchOperationsCollector patch(final Object source) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		
		return patch(source, getSerializationContext());
	}
	
	private static SerializationContext getSerializationContext() {
		if (serializationContext == null) {
			serializationContext = new SerializationContext();
		}
		
		return serializationContext;
	}

	public static JsonPatchOperationsCollector patch(final Object source, final SerializationContext context) {
		Objects.requireNonNull(source, "Argument 'source' must not be null");
		Objects.requireNonNull(context, "Argument 'context' must not be null");
		
		ObjectVisitingReader reader = new ObjectVisitingReader(source, context);
		JsonTreeBuildingVisitor visitor = new JsonTreeBuildingVisitor();
		
		reader.accept(visitor);
		
		return handleJsonElement(visitor.getVisitingResult());
	}

	private static JsonPatchOperationsCollector handleJsonElement(JsonElement jsonElement) {
		if (jsonElement instanceof JsonType) {
			return new JsonPatchOperationsCollector((JsonType) jsonElement);
		} else {
			throw new IllegalArgumentException("Argument 'source' must be "
					+ "convertible to a valid JsonType");
		}
	}
}

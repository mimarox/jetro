/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

/**
 * The entry point for Jetro JSON Binding. It contains methods converting Java objects
 * to and from JSON {@link String}s. Additionally it allows reading JSON data directly from
 * {@link InputStream}s.
 * 
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectMapper {
	private static SerializationContext serializationContext;
	private static DeserializationContext deserializationContext;
	
	private static DeserializationContext getDeserializationContext() {
		if (deserializationContext == null) {
			deserializationContext = DeserializationContext.getDefault();
		}
		
		return deserializationContext;
	}
	
	private static SerializationContext getSerializationContext() {
		if (serializationContext == null) {
			serializationContext = new SerializationContext();
		}
		
		return serializationContext;
	}

	/**
	 * Entry method for merging an object into a {@link JsonVisitor}. Call
	 * {@link #merge(Object)}.{@link ObjectMerger#into(JsonVisitor) into(JsonVisitor)}
	 * to perform the merger.
	 * <p>
	 * See {@link SerializationContext} for detailed information about what kinds of
	 * objects can be merged.
	 * 
	 * @param toMerge The object to merge
	 * @return An instance of {@link ObjectMerger}
	 * @see SerializationContext
	 */
	public ObjectMerger merge(final Object toMerge) {
		return new ObjectMerger(toMerge);
	}
	
	/**
	 * Entry method for merging an object into a {@link JsonVisitor}. Call
	 * {@link #merge(Object, SerializationContext)}.{@link ObjectMerger#into(JsonVisitor)
	 * into(JsonVisitor)} to perform the merger.
	 * <p>
	 * See {@link SerializationContext} for detailed information about what kinds of
	 * objects can be merged and how the SerializationContext can be customized.
	 * 
	 * @param toMerge The object to merge
	 * @param context The serialization context
	 * @return An instance of {@link ObjectMerger}
	 * @see SerializationContext
	 */
	public ObjectMerger merge(final Object toMerge, final SerializationContext context) {
		return new ObjectMerger(toMerge, context);
	}
	
	public String toJson(final Object object) {
		return toJson(object, getSerializationContext());
	}

	public String toJson(final Object object, final SerializationContext context) {
		JsonReturningVisitor receiver = new JsonReturningVisitor();
		ObjectVisitingReader reader = new ObjectVisitingReader(object, context);
		reader.accept(receiver);
		return receiver.getVisitingResult();
	}
	
	public <T> T fromJson(final String json, final Class<T> targetClass) {
		return fromJson(json, TypeToken.of(targetClass));
	}
	
	public <T> T fromJson(final String json, final TypeToken<T> targetTypeToken) {
		try (StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)))) {
			return fromJson(reader, targetTypeToken, getDeserializationContext());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public <T> T fromJson(final String json, final Class<T> targetClass,
			final DeserializationContext context) {
		return fromJson(json, TypeToken.of(targetClass), context);
	}
	
	public <T> T fromJson(final String json, final TypeToken<T> targetTypeToken,
			final DeserializationContext context) {
		try (StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)))) {
			return fromJson(reader, targetTypeToken, context);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T> T fromJson(final InputStream in, final Class<T> targetClass) {
		return fromJson(in, TypeToken.of(targetClass));
	}
	
	public <T> T fromJson(final InputStream in, final TypeToken<T> targetTypeToken) {
		try (StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(
				new InputStreamReader(in, "UTF-8")))) {
			return fromJson(reader, targetTypeToken, getDeserializationContext());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T> T fromJson(final InputStream in, final Class<T> targetClass,
			final DeserializationContext context) {
		return fromJson(in, TypeToken.of(targetClass), context);
	}
	
	public <T> T fromJson(final InputStream in, final TypeToken<T> targetTypeToken,
			final DeserializationContext context) {
		try (StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(
				new InputStreamReader(in, "UTF-8")))) {
			return fromJson(reader, targetTypeToken, context);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private <T> T fromJson(final VisitingReader reader, final TypeToken<T> targetTypeToken,
			final DeserializationContext context) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		ObjectBuildingVisitor visitor = new ObjectBuildingVisitor(targetTypeToken,
				context);
		reader.accept(visitor);
		return targetTypeToken.getRawType().cast(visitor.getVisitingResult());
	}
}

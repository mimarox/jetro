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
package net.sf.jetro.object.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.object.reflect.TypeToken;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class SerializationContext extends RenderContext {
	private static final int NON_PRIMITIVE_OFFSET = 5;
	private static final NullSerializer NULL_SERIALIZER = new NullSerializer();
	private static final NopSerializer NOP_SERIALIZER = new NopSerializer();

	private List<TypeSerializer<?>> typeSerializers = new ArrayList<>();
	private Map<TypeToken<?>, Function<Object, String>> stringSerializers = new HashMap<>(); 

	private boolean throwNoSerializerException;

	public SerializationContext() {
		// Add primitive serializers
		typeSerializers.add(new CharSequenceSerializer());
		typeSerializers.add(new NumberSerializer());
		typeSerializers.add(new BooleanSerializer());
		typeSerializers.add(new CharacterSerializer());
		typeSerializers.add(new EnumSerializer<>());

		// Add complex serializers
		typeSerializers.add(new ArraySerializer(this));
		typeSerializers.add(new IterableSerializer(this));
		typeSerializers.add(new MapSerializer(this));
		typeSerializers.add(new BeanSerializer(this));
		
		stringSerializers.put(TypeToken.of(String.class), object -> object.toString());
	}

	/**
	 * @deprecated Use {@link #addTypeSerializer(TypeSerializer<?>)} instead
	 */
	public SerializationContext addSerializer(TypeSerializer<?> serializer) {
		return addTypeSerializer(serializer);
	}

	public SerializationContext addTypeSerializer(TypeSerializer<?> serializer) {
		if (serializer != null) {
			// Add after primitive but before generic complex serializers
			typeSerializers.add(NON_PRIMITIVE_OFFSET, serializer);
		}

		return this;
	}
	
	public SerializationContext addStringSerializer(TypeToken<?> typeToken,
			Function<Object, String> serializer) {
		if (typeToken != null && serializer != null) {
			stringSerializers.put(typeToken, serializer);
		}
		
		return this;
	}

	public SerializationContext setThrowNoSerializerException(boolean throwNoSerializerException) {
		this.throwNoSerializerException = throwNoSerializerException;
		return this;
	}

	/**
	 * @deprecated Use {@link #getTypeSerializer(Object)} instead
	 */
	@Deprecated
	public TypeSerializer<Object> getSerializer(Object toSerialize) {
		return getTypeSerializer(toSerialize);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TypeSerializer<Object> getTypeSerializer(Object toSerialize) {
		if (toSerialize == null) {
			if (isSerializeNulls()) {
				return NULL_SERIALIZER;
			} else {
				return NOP_SERIALIZER;
			}
		}

		TypeSerializer<Object> serializer = null;

		for (TypeSerializer<?> candidate : typeSerializers) {
			if (candidate.canSerialize(toSerialize)) {
				serializer = (TypeSerializer) candidate;
				break;
			}
		}

		if (serializer != null) {
			return serializer;
		} else if (throwNoSerializerException) {
			throw new IllegalStateException("No serializer found for object [" + toSerialize + "]");
		} else {
			return NOP_SERIALIZER;
		}
	}
	
	public Function<Object, String> getStringSerializer(TypeToken<?> typeToken) {
		if (stringSerializers.containsKey(typeToken)) {
			return stringSerializers.get(typeToken);
		} else {
			Optional<Function<Object, String>> optional = stringSerializers.entrySet()
					.parallelStream().filter(entry -> entry.getKey().getRawType()
							.equals(typeToken.getRawType()))
					.map(entry -> entry.getValue()).findFirst();
			
			if (optional.isPresent()) {
				return optional.get();
			} else if (throwNoSerializerException) {
				throw new IllegalStateException("No serializer found for typeToken ["
						+ typeToken + "]");
			} else {
				return object -> object.toString();
			}
		}
	}
}

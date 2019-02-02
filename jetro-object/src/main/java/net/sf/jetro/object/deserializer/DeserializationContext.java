/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object.deserializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import net.sf.jetro.object.exception.DeserializationException;
import net.sf.jetro.object.reflect.TypeToken;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class DeserializationContext {
	private final ObjectConstructor objectConstructor = new ObjectConstructor();
	private final Map<TypeToken<?>, Function<String, Object>> stringDeserializers = new HashMap<>();
	private final Map<TypeToken<?>, Function<Number, Object>> numberDeserializers = new HashMap<>();
	private boolean throwOnMissingDeserializer = true;
	
	public DeserializationContext() {
		addNumberDeserializer(TypeToken.of(Number.class), value -> value);
		addNumberDeserializer(TypeToken.of(byte.class), value -> value.byteValue());
		addNumberDeserializer(TypeToken.of(Byte.class), value -> value.byteValue());
		addNumberDeserializer(TypeToken.of(short.class), value -> value.shortValue());
		addNumberDeserializer(TypeToken.of(Short.class), value -> value.shortValue());
		addNumberDeserializer(TypeToken.of(int.class), value -> value.intValue());
		addNumberDeserializer(TypeToken.of(Integer.class), value -> value.intValue());
		addNumberDeserializer(TypeToken.of(long.class), value -> value.longValue());
		addNumberDeserializer(TypeToken.of(Long.class), value -> value.longValue());
		addNumberDeserializer(TypeToken.of(float.class), value -> value.floatValue());
		addNumberDeserializer(TypeToken.of(Float.class), value -> value.floatValue());
		addNumberDeserializer(TypeToken.of(double.class), value -> value.doubleValue());
		addNumberDeserializer(TypeToken.of(Double.class), value -> value.doubleValue());
	}
	
	public <T> DeserializationContext addInstanceCreator(TypeToken<T> typeToken,
			InstanceCreator<T> instanceCreator) {
		if (typeToken != null && instanceCreator != null) {
			objectConstructor.addInstanceCreator(typeToken, instanceCreator);
		}

		return this;
	}

	public <T> T createInstanceOf(TypeToken<T> type) {
		return (T) objectConstructor.constructFrom(type);
	}

	@SuppressWarnings("unchecked")
	public <T> DeserializationContext addStringDeserializer(TypeToken<T> typeToken,
			Function<String, T> deserializer) {
		if (typeToken != null && deserializer != null) {
			stringDeserializers.put(typeToken, (Function<String, Object>) deserializer);
		}

		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> DeserializationContext addNumberDeserializer(TypeToken<T> typeToken,
			Function<Number, T> deserializer) {
		if (typeToken != null && deserializer != null) {
			numberDeserializers.put(typeToken, (Function<Number, Object>) deserializer);
		}

		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValueForType(TypeToken<T> typeToken, String value) {
		if (stringDeserializers.containsKey(typeToken)) {
			return (T) stringDeserializers.get(typeToken).apply(value);
		} else {
			Optional<Function<String, Object>> optional = stringDeserializers.entrySet()
					.parallelStream().filter(entry -> entry.getKey().getRawType()
							.equals(typeToken.getRawType()))
					.map(entry -> entry.getValue()).findFirst();
			
			if (optional.isPresent()) {
				return (T) optional.get().apply(value);
			} else if (throwOnMissingDeserializer) {
				throw new DeserializationException("Found no deserializer for type " +
						typeToken.getType());
			} else {
				return null;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValueForType(TypeToken<T> typeToken, Number value) {
		if (numberDeserializers.containsKey(typeToken)) {
			return (T) numberDeserializers.get(typeToken).apply(value);
		} else {
			Optional<Function<Number, Object>> optional = numberDeserializers.entrySet()
					.parallelStream().filter(entry -> entry.getKey().getRawType()
							.equals(typeToken.getRawType()))
					.map(entry -> entry.getValue()).findFirst();

			if (optional.isPresent()) {
				return (T) optional.get().apply(value);
			} else if (throwOnMissingDeserializer) {
				throw new DeserializationException("Found no deserializer for type " +
						typeToken.getType());
			} else {
				return null;
			}
		}
	}
	
	public void setThrowOnMissingDeserializer(boolean throwOnMissingDeserializer) {
		this.throwOnMissingDeserializer = throwOnMissingDeserializer;
	}
	
	public static DeserializationContext getDefault() {
		DeserializationContext context = new DeserializationContext();
		
		context.addInstanceCreator(TypeToken.of(List.class), typeToken -> new ArrayList<>());
		context.addInstanceCreator(TypeToken.of(Map.class), typeToken -> new HashMap<>());
		
		context.addNumberDeserializer(TypeToken.of(Date.class), value -> new Date(value.longValue()));
		
		return context;
	}
}

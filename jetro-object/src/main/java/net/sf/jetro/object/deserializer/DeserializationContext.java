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
import java.util.function.Function;

import net.sf.jetro.object.exception.DeserializationException;
import net.sf.jetro.object.reflect.TypeToken;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class DeserializationContext {
	private final ObjectConstructor objectConstructor = new ObjectConstructor();
	private final Map<TypeToken<?>, Function<String, ?>> stringDeserializers = new HashMap<>();
	private final Map<TypeToken<?>, Function<Number, ?>> numberDeserializers = new HashMap<>();
	private boolean throwOnMissingDeserializer = true;
	
	public <T> void addInstanceCreator(TypeToken<T> typeToken, InstanceCreator<T> instanceCreator) {
		objectConstructor.addInstanceCreator(typeToken, instanceCreator);
	}

	public <T> T createInstanceOf(TypeToken<T> type) {
		return (T) objectConstructor.constructFrom(type);
	}

	public <T> void addStringDeserializer(TypeToken<T> typeToken, Function<String, T> deserializer) {
		stringDeserializers.put(typeToken, deserializer);
	}
	
	public <T> void addNumberDeserializer(TypeToken<T> typeToken, Function<Number, T> deserializer) {
		numberDeserializers.put(typeToken, deserializer);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValueForType(TypeToken<T> typeToken, String value) {
		if (stringDeserializers.containsKey(typeToken)) {
			return (T) stringDeserializers.get(typeToken).apply(value);
		} else {
			if (throwOnMissingDeserializer) {
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
			if (throwOnMissingDeserializer) {
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
		context.addNumberDeserializer(TypeToken.of(Number.class), value -> value);
		context.addNumberDeserializer(TypeToken.of(byte.class), value -> value.byteValue());
		context.addNumberDeserializer(TypeToken.of(Byte.class), value -> value.byteValue());
		context.addNumberDeserializer(TypeToken.of(short.class), value -> value.shortValue());
		context.addNumberDeserializer(TypeToken.of(Short.class), value -> value.shortValue());
		context.addNumberDeserializer(TypeToken.of(int.class), value -> value.intValue());
		context.addNumberDeserializer(TypeToken.of(Integer.class), value -> value.intValue());
		context.addNumberDeserializer(TypeToken.of(long.class), value -> value.longValue());
		context.addNumberDeserializer(TypeToken.of(Long.class), value -> value.longValue());
		context.addNumberDeserializer(TypeToken.of(float.class), value -> value.floatValue());
		context.addNumberDeserializer(TypeToken.of(Float.class), value -> value.floatValue());
		context.addNumberDeserializer(TypeToken.of(double.class), value -> value.doubleValue());
		context.addNumberDeserializer(TypeToken.of(Double.class), value -> value.doubleValue());
		
		return context;
	}
}

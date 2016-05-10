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
package net.sf.jetro.object.serializer;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class SerializationContext extends RenderContext {
	private static final int NON_PRIMITIVE_OFFSET = 4;
	private static final NullSerializer NULL_SERIALIZER = new NullSerializer();
	private static final NopSerializer NOP_SERIALIZER = new NopSerializer();

	private List<TypeSerializer<?>> serializers = new ArrayList<TypeSerializer<?>>();

	private boolean throwNoSerializerException;

	public SerializationContext() {
		// Add primitive serializers
		serializers.add(new CharSequenceSerializer());
		serializers.add(new NumberSerializer());
		serializers.add(new BooleanSerializer());
		serializers.add(new CharacterSerializer());

		// Add complex serializers
		serializers.add(new ArraySerializer(this));
		serializers.add(new IterableSerializer(this));
		serializers.add(new MapSerializer(this));
		serializers.add(new BeanSerializer(this));
	}

	public SerializationContext addSerializer(TypeSerializer<?> serializer) {
		if (serializer != null) {
			// Add after primitive but before generic complex serializers
			serializers.add(NON_PRIMITIVE_OFFSET, serializer);
		}

		return this;
	}

	public SerializationContext setThrowNoSerializerException(boolean throwNoSerializerException) {
		this.throwNoSerializerException = throwNoSerializerException;
		return this;
	}

	public TypeSerializer<Object> getSerializer(Object toSerialize) {
		if (toSerialize == null) {
			if (isSerializeNulls()) {
				return NULL_SERIALIZER;
			} else {
				return NOP_SERIALIZER;
			}
		}

		TypeSerializer<Object> serializer = null;

		for (TypeSerializer<?> candidate : serializers) {
			if (candidate.canSerialize(toSerialize)) {
				serializer = (TypeSerializer) candidate;
				break;
			}
		}

		if (serializer != null) {
			return serializer;
		} else if (serializer == null && throwNoSerializerException) {
			throw new IllegalStateException("No serializer found for object [" + toSerialize + "]");
		} else {
			return NOP_SERIALIZER;
		}
	}
}

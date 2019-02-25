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

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class ArraySerializer implements TypeSerializer<Object> {
	private SerializationContext context;

	public ArraySerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize != null && toSerialize.getClass().isArray();
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		JsonArrayVisitor<?> arrayVisitor = recipient.visitArray();

		for (int i = 0, length = Array.getLength(toSerialize); i < length; i++) {
			Object element = Array.get(toSerialize, i);
			TypeSerializer<Object> serializer = context.getTypeSerializer(element);
			serializer.serialize(element, arrayVisitor);
		}

		arrayVisitor.visitEnd();
	}
}

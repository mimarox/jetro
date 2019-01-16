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

import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class MapSerializer implements TypeSerializer<Map<?, ?>> {
	private SerializationContext context;

	public MapSerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Map;
	}

	@Override
	public void serialize(Map<?, ?> toSerialize, JsonVisitor<?> recipient) {
		if (toSerialize == null) {
			recipient.visitNullValue();
			return;
		}

		JsonObjectVisitor<?> objectVisitor = recipient.visitObject();

		for (Entry<?, ?> entry : toSerialize.entrySet()) {
			TypeSerializer<Object> serializer = context.getSerializer(entry.getValue());

			objectVisitor.visitProperty(entry.getKey().toString());
			serializer.serialize(entry.getValue(), objectVisitor);
		}

		objectVisitor.visitEnd();
	}
}

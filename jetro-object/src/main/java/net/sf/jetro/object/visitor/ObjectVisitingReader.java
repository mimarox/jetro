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
package net.sf.jetro.object.visitor;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectVisitingReader implements VisitingReader {
	private SerializationContext context;
	private Object toSerialize;

	/**
	 * @deprecated Use {@link #ObjectVisitingReader(Object,SerializationContext)} instead
	 */
	public ObjectVisitingReader(SerializationContext context, Object toSerialize) {
		this(toSerialize, context);
	}

	public ObjectVisitingReader(Object toSerialize, SerializationContext context) {
		this.context = context;
		this.toSerialize = toSerialize;
	}

	@Override
	public void accept(JsonVisitor<?> visitor) {
		TypeSerializer<Object> serializer = context.getTypeSerializer(toSerialize);
		serializer.serialize(toSerialize, visitor);
	}
}

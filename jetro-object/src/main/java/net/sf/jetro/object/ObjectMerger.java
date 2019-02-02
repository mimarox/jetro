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
package net.sf.jetro.object;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 * @deprecated Use {@link ObjectMapper#toJson(Object)} and
 * {@link ObjectMapper#toJson(Object, SerializationContext)} directly.
 */
@Deprecated
public class ObjectMerger {
	private SerializationContext context;
	private Object toMerge;

	public ObjectMerger(Object toMerge) {
		this(toMerge, new SerializationContext());
	}

	/**
	 * @deprecated Use {@link #ObjectMerger(Object,SerializationContext)} instead
	 */
	@Deprecated
	public ObjectMerger(SerializationContext context, Object toMerge) {
		this(toMerge, context);
	}

	public ObjectMerger(Object toMerge, SerializationContext context) {
		this.context = context;
		this.toMerge = toMerge;
	}

	/**
	 * @deprecated Use {@link #mergeInto(JsonVisitor<?>)} instead
	 */
	@Deprecated
	public void into(JsonVisitor<?> visitor) {
		mergeInto(visitor);
	}

	public void mergeInto(JsonVisitor<?> visitor) {
		ObjectVisitingReader reader = new ObjectVisitingReader(toMerge, context);
		reader.accept(visitor);
	}
}

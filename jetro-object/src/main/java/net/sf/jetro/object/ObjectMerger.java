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

import java.util.Objects;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

/**
 * Class with the fluent API method {@link #into(JsonVisitor)}. To use it call
 * {@link ObjectMapper#merge(Object)} or {@link ObjectMapper#merge(Object, SerializationContext)}.
 * 
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectMerger {
	private SerializationContext context;
	private Object toMerge;

	ObjectMerger(final Object toMerge) {
		this(toMerge, new SerializationContext());
	}

	ObjectMerger(final Object toMerge, final SerializationContext context) {
		Objects.requireNonNull(toMerge);
		Objects.requireNonNull(context);
		
		this.toMerge = toMerge;
		this.context = context;
	}

	/**
	 * Merges an object into the given {@link JsonVisitor}.
	 * 
	 * @param visitor The visitor to merge into
	 */
	public void into(final JsonVisitor<?> visitor) {
		Objects.requireNonNull(visitor);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(toMerge, context);
		reader.accept(visitor);
	}
}

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

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.visitor.JsonVisitor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class DeserializationContext {
	private ObjectConstructor objectConstructor = new ObjectConstructor();
	private List<TypeDeserializer<?>> arrayDeserializers = new ArrayList<TypeDeserializer<?>>();
	private List<TypeDeserializer<?>> objectDeserializers = new ArrayList<TypeDeserializer<?>>();

	public DeserializationContext() {
		objectDeserializers.add(new BeanDeserializer(this));
	}

	public <T> void addInstanceCreator(TypeToken<T> typeToken, InstanceCreator<T> instanceCreator) {
		objectConstructor.addInstanceCreator(typeToken, instanceCreator);
	}

	public <T> T createInstanceOf(Type type) {
		return (T) objectConstructor.constructFrom(type);
	}

	public <R> JsonVisitor<R> getArrayVisitorFor(TypeToken<R> typeToken) {
		return null;
	}

	public <R> JsonVisitor<R> getObjectVisitorFor(TypeToken<R> typeToken) {
		for (TypeDeserializer<?> candidate : objectDeserializers) {
			if (candidate.canDeserialize((TypeToken) typeToken)) {
				return (JsonVisitor<R>) candidate.getVisitorFor((TypeToken) typeToken);
			}
		}

		return null;
	}
}

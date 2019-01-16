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

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;

import java.io.InputStream;
import java.io.StringReader;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectMapper {
	public ObjectMerger merge(Object toMerge) {
		return new ObjectMerger(toMerge);
	}

	public String toJson(Object object) {
		JsonReturningVisitor receiver = new JsonReturningVisitor();
		merge(object).into(receiver);
		return receiver.getVisitingResult();
	}

	public <T> T fromJson(String json, Class<T> targetClass) {
		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));
		ObjectBuildingVisitor visitor = new ObjectBuildingVisitor(new DeserializationContext(), TypeToken.of(targetClass));
		reader.accept(visitor);
		return targetClass.cast(visitor.getVisitingResult());
	}

	public <T> T fromJson(InputStream in, Class<T> clazz) {
		return null;
	}
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class IterableSerializerTest {

	@Test
	public void testSerialization() {
		TypeSerializer<Object> serializer = getSerializer();
		
		List<String> strings = Arrays.asList(new String[]{ "foo", "bar" });
		
		serializer.serialize(new ArrayList<String>(strings),
				new UniformChainedJsonVisitor<Object>() {});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TypeSerializer<Object> getSerializer() {
		return (TypeSerializer) new IterableSerializer(new SerializationContext());
	}
}

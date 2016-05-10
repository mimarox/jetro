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
package net.sf.jetro.object.reflect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by matthias.rothe on 26.03.14.
 */
public class TypeTokenTest {

	@Test(expectedExceptions = RuntimeException.class)
	public void testNoSubClassTypeToken() {
		TypeToken<String> token = new TypeToken<String>();
	}

	@Test
	public void testNonGenericTypeToken() {
		TypeToken<String> token = new TypeToken<String>(){};
		assertEquals(String.class, token.getType());
		assertEquals(String.class, token.getRawType());
	}

	@Test
	public void testSimpleGenericTypeToken() {
		TypeToken<List<String>> token = new TypeToken<List<String>>(){};

		Type type = token.getType();
		assertTrue(type instanceof ParameterizedType, "expected ParameterizedType");

		ParameterizedType parameterizedType = (ParameterizedType) type;
		assertEquals(List.class, parameterizedType.getRawType());
		assertEquals(String.class, parameterizedType.getActualTypeArguments()[0]);

		assertEquals(List.class, token.getRawType());
	}

	@Test
	public void testWildcardGenericTypeToken() {
		TypeToken<List<? super Integer>> token = new TypeToken<List<? super Integer>>(){};
	}

	@Test
	public <T> void testTypeVariableTypeToken() {
		TypeToken<List<T>> token = new TypeToken<List<T>>(){};
	}
}

/*
 * #%L
 * Jetro Patch
 * %%
 * Copyright (C) 2013 - 2020 The original author or authors.
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
package net.sf.jetro.patch.pointer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

public class JsonPointerTest {
	
	@Test
	@DataBinding(propertiesPrefix = "mapJsonPathToJsonPointer")
	public void shouldCreateFromJsonPath(@TestInput(name = "jsonPath") final String jsonPath,
										 @TestInput(name = "jsonPointer") final String jsonPointer) {
		JsonPath path = JsonPath.compile(jsonPath);
		
		JsonPointer actual = JsonPointer.fromJsonPath(path);
		JsonPointer expected = JsonPointer.compile(jsonPointer);
		
		assertNotNull(actual);
		assertNotNull(expected);
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding(propertiesPrefix = "mapJsonPathToJsonPointer")
	public void shouldConvertToJsonPath(@TestInput(name = "jsonPath") final String jsonPath,
										@TestInput(name = "jsonPointer") final String jsonPointer) {
		JsonPointer pointer = JsonPointer.compile(jsonPointer);
		
		assertNotNull(pointer);
		
		JsonPath actual = pointer.toJsonPath();
		JsonPath expected = JsonPath.compile(jsonPath);
		
		assertNotNull(actual);
		assertNotNull(expected);
		assertEquals(actual, expected);
	}
	
	@Test
	@DataBinding
	public void shouldCompile(@TestInput(name = "jsonPointer") final String jsonPointer) {
		JsonPointer pointer = JsonPointer.compile(jsonPointer);
		String actual = pointer.toString();
		
		assertEquals(actual, jsonPointer);
	}
	
	@Test(expectedExceptions = JsonPointerException.class,
			expectedExceptionsMessageRegExp = "jsonPointer is not a valid JsonPointer, " + 
			"since it doesn't start with a separator \\(/\\)")
	public void shouldNotCompile() {
		JsonPointer.compile("foo");
	}
	
	@Test
	public void shouldAppend() {
		JsonPointer jsonPointer = JsonPointer.compile("/0");
		JsonPointer actual = jsonPointer.append(new ArrayIndexPointerElement(0));
		JsonPointer expected = JsonPointer.compile("/0/0");
		
		assertNotNull(actual);
		assertNotNull(expected);
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldHavePropertyNameAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/0/foo");
		assertTrue(jsonPointer.hasPropertyNameAt(2));
	}
	
	
	@Test
	public void shouldNotHavePropertyNameAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/0/0");
		assertFalse(jsonPointer.hasPropertyNameAt(2));
	}
	
	@Test
	public void shouldHaveArrayIndexAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/foo/0");
		assertTrue(jsonPointer.hasArrayIndexAt(2));
	}
	
	@Test
	public void shouldNotHaveArrayIndexAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/foo/bar");
		assertFalse(jsonPointer.hasArrayIndexAt(2));
	}
	
	@Test
	public void shouldGetPropertyNameAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/0/foo");
		
		String actual = jsonPointer.getPropertyNameAt(2);
		String expected = "foo";
		
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldNotGetPropertyNameAt() {
		JsonPointer.compile("/0").getPropertyNameAt(1);
	}
	
	@Test
	public void shouldGetArrayIndexAt() {
		JsonPointer jsonPointer = JsonPointer.compile("/foo/5");
		
		int actual = jsonPointer.getArrayIndexAt(2);
		int expected = 5;
		
		assertEquals(actual, expected);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldNotGetArrayIndexAt() {
		JsonPointer.compile("/foo").getArrayIndexAt(1);
	}
	
	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldNotRemoveLastElementFromRootPointer() {
		JsonPointer.compile("").removeLastElement();
	}
}

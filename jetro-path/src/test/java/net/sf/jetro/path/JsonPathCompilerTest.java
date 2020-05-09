/*
 * #%L
 * Jetro JsonPath
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
package net.sf.jetro.path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;
import net.sf.testng.databinding.TestOutput;

import org.testng.annotations.Test;

@DataBinding
public class JsonPathCompilerTest {
	private JsonPathCompiler compiler = new JsonPathCompiler();

	@Test
	public void shouldCompile(@TestInput(name = "jsonPath") final String jsonPath) {
		String actual = compiler.compile(jsonPath).toString();
		assertEquals(actual, jsonPath);
	}

	@Test
	public void shouldNotCompile(@TestInput(name = "jsonPath") final String jsonPath,
			@TestOutput(name = "errorMessage") final String errorMessage) {
		try {
			compiler.compile(jsonPath);
			fail(jsonPath + " compiled, but shouldn't have");
		} catch (JsonPathCompilerException e) {
			assertEquals(e.getMessage(), errorMessage);
		}
	}
}
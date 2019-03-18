/*
 * #%L
 * Jetro Tree
 * %%
 * Copyright (C) 2013 - 2019 The original author or authors.
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
package net.sf.jetro.tree.builder;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.testng.annotations.Test;

import net.sf.jetro.stream.visitor.LazilyParsedNumber;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;

public class JsonTreeBuilderTest {

	@Test
	public void shouldBuildJsonTree() {
		String json = "{\"foo\":null,\"bar\":[true,\"hello\",2]}";

		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonElement actual = builder.build(json);

		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("foo", new JsonNull()));
		expected.add(new JsonProperty("bar", new JsonArray(Arrays.asList(
				new JsonBoolean(true), new JsonString("hello"),
				new JsonNumber(new LazilyParsedNumber("2"))))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeWithTransformers() {
		String json = "[\"0\"]";
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonElement root = builder.build(json,
				new StringValueTransformingVisitor("1"),
				new StringValueTransformingVisitor("2"),
				new StringValueTransformingVisitor("3"));
		
		String actual = root.toJson();
		String expected = "[\"0123\"]";
		
		assertEquals(actual, expected);
	}
}
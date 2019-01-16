/*
 * #%L
 * Jetro Tree
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
package net.sf.jetro.tree.visitor;

import static org.testng.Assert.assertEquals;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

import org.testng.annotations.Test;

public class VisitingJsonRendererTest {

	@Test
	public void shouldRender() {
		// Setup JSON tree
		JsonObject root = new JsonObject();

		JsonProperty foo = new JsonProperty("foo");
		foo.setValue(new JsonNull(JsonPath.compile("$.foo")));

		JsonArray barArray = new JsonArray(JsonPath.compile("$.bar"));
		barArray.add(new JsonBoolean(JsonPath.compile("$.bar[0]"), true));
		barArray.add(new JsonString(JsonPath.compile("$.bar[1]"), "hello"));
		barArray.add(new JsonNumber(JsonPath.compile("$.bar[2]"), 2));

		JsonProperty bar = new JsonProperty("bar");
		bar.setValue(barArray);

		root.add(foo);
		root.add(bar);

		// Setup visiting reader & visitor
		VisitingReader visitingReader = new JsonElementVisitingReader(root);
		JsonVisitor<String> visitor = new JsonReturningVisitor();

		// Render
		visitingReader.accept(visitor);
		String actual = visitor.getVisitingResult();
		String expected = "{\"foo\":null,\"bar\":[true,\"hello\",2]}";

		// Assert
		assertEquals(actual, expected);
	}
}
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

import java.io.StringReader;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

import org.testng.annotations.Test;

public class JsonTreeBuildingVisitorTest {

	@Test
	public void shouldBuildJsonTree() {
		String json = "{\"foo\":null,\"bar\":[true,\"hello\",2]}";

		StringReader in = new StringReader(json);
		JsonReader reader = new JsonReader(in);

		VisitingReader visitingReader = new StreamVisitingReader(reader);
		JsonTreeBuildingVisitor builder = new JsonTreeBuildingVisitor();

		visitingReader.accept(builder);
		JsonElement root = builder.getVisitingResult();

		visitingReader = new JsonElementVisitingReader(root);
		JsonVisitor<String> visitor = new JsonReturningVisitor();
		visitingReader.accept(visitor);

		String actual = visitor.getVisitingResult();

		assertEquals(actual, json);
	}
}
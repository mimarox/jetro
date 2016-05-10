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
package net.sf.jetro.tree.transformations;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;
import org.testng.annotations.Test;

import java.io.StringReader;

/**
 * Created by matthias.rothe on 22.02.14.
 */
public class ReplacingTransformationsOnMixedTest {
	private static final JsonPath PATH = JsonPath.compile("$.favorites[*]");

	public static void main(String[] args) {
		ReplacingTransformationsOnMixedTest test = new ReplacingTransformationsOnMixedTest();

		for (int i = 0; i < 10; i++) {
			test.runPerformanceTest();
		}
	}

	private void runPerformanceTest() {
		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++) {
			performTransformations(
					"{" +
							"\"favorites\": [" +
							"\"/content/page/1\", " +
							"\"/content/page/2\", " +
							"\"/content/page/3\"" +
							"]" +
							"}");
		}

		long end = System.currentTimeMillis();

		System.out.println("Dauer für 1'000'000 Durchläufe: " + (end - start) + "ms");
	}

	@Test
	public void testReplacingTransformations() {
		String out = performTransformations(
				"{" +
						"\"favorites\": [" +
						"\"/content/page/1\", " +
						"\"/content/page/2\", " +
						"\"/content/page/3\"" +
						"]" +
						"}");
		System.out.println(out);
	}

	private String performTransformations(String json) {
		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));

		JsonReturningVisitor writer = new JsonReturningVisitor();

		ChainedJsonVisitor transformer = new PathAwareJsonVisitor(writer) {
			@Override
			protected String doBeforeVisitValue(String value) {
				JsonPath currentPath = currentPath();

				if (currentPath.matches(PATH)) {
					JsonObject favorite = new JsonObject();
					favorite.add(new JsonProperty("page", new JsonString(value)));
					favorite.add(new JsonProperty("title", new JsonString("Title for: " + value)));

					favorite.mergeInto(getNextVisitor());

					return null;
				}

				return value;
			}
		};

		reader.accept(transformer);

		return writer.getVisitingResult();
	}
}

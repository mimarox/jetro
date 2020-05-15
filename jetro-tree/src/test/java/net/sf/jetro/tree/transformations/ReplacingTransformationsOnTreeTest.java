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

import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.tree.builder.JsonTreeBuilder;
import org.testng.annotations.Test;

/**
 * Created by matthias.rothe on 20.02.14.
 */
public class ReplacingTransformationsOnTreeTest {

	public static void main(String[] args) {
		ReplacingTransformationsOnTreeTest test = new ReplacingTransformationsOnTreeTest();

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
		JsonTreeBuilder treeBuilder = new JsonTreeBuilder();
		JsonObject root = (JsonObject) treeBuilder.buildFrom(json);
		JsonArray oldFavorites = (JsonArray) root.asMap().remove("favorites");
		JsonArray newFavorites = new JsonArray();

		for (JsonElement element : oldFavorites) {
			JsonString oldValue = (JsonString) element;

			JsonObject object = new JsonObject();
			object.add(new JsonProperty("page", oldValue));
			object.add(new JsonProperty("title", new JsonString("Title for: " + oldValue.getValue())));

			newFavorites.add(object);
		}

		root.add(new JsonProperty("favorites", newFavorites));
		return root.toJson();
	}
}

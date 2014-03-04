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
		JsonObject root = (JsonObject) treeBuilder.build(json);
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

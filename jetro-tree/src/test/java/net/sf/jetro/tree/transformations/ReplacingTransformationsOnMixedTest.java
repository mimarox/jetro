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

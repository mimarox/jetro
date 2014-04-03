package net.sf.jetro.stream.transformations;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import org.testng.annotations.Test;

import java.io.StringReader;

/**
 * Created by matthias.rothe on 20.02.14.
 */
public class ReplacingTransformationsOnStreamTest {
	private JsonReturningVisitor writer = new JsonReturningVisitor();
	private ChainedJsonVisitor<String> transformer = new UniformChainedJsonVisitor<String>(writer) {
		@Override
		protected String beforeVisitValue(String value) {
//				JsonPath currentPath = currentPath();

//				if (currentPath.matches(JsonPath.compile("$.favorites[*]"))) {
			JsonObjectVisitor objectVisitor = getNextVisitor().visitObject();
			objectVisitor.visitProperty("path");
			objectVisitor.visitValue(value);
			objectVisitor.visitProperty("title");
			objectVisitor.visitValue("Title for: " + value);
			objectVisitor.visitEnd();

			return null;
//				}

//				return value;
		}
	};

	public static void main(String[] args) throws InterruptedException {
		ReplacingTransformationsOnStreamTest test = new ReplacingTransformationsOnStreamTest();

		for (int i = 0; i < 50; i++) {
			test.runPerformanceTest();
		}

		Thread.sleep(1000 * 60 * 60 * 24);
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
		reader.accept(transformer);
		return writer.getVisitingResult();
	}
}

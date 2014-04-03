package net.sf.jetro.object.transformations;

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.transformations.beans.Favorite;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import org.testng.annotations.Test;

import java.io.StringReader;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class ReplacingTransformationsOnMixedObjectTest {

	public static void main(String[] args) {
		ReplacingTransformationsOnMixedObjectTest test = new ReplacingTransformationsOnMixedObjectTest();

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
		final ObjectMapper mapper = new ObjectMapper();

		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));

		JsonReturningVisitor writer = new JsonReturningVisitor();

		ChainedJsonVisitor transformer = new UniformChainedJsonVisitor(writer) {
			@Override
			protected String beforeVisitValue(String value) {
				Favorite favorite = new Favorite();
				favorite.setPage(value);
				favorite.setTitle("Title for: " + value);

				mapper.merge(favorite).into(getNextVisitor());

				return null;
			}
		};

		reader.accept(transformer);

		return writer.getVisitingResult();
	}
}

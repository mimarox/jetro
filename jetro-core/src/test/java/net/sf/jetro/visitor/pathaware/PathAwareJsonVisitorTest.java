package net.sf.jetro.visitor.pathaware;

import static org.testng.Assert.assertEquals;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.VisitingReader;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathAwareJsonVisitorTest {

	@Test
	public void shouldProduceCorrectPaths() {
		List<JsonPath> expecteds = Arrays.asList(new JsonPath[] { new JsonPath(), JsonPath.compile("$.key"),
				JsonPath.compile("$.key[0]"), JsonPath.compile("$.key[1]"), JsonPath.compile("$.key[2]"),
				JsonPath.compile("$.key[3]"), JsonPath.compile("$.key[4]"), JsonPath.compile("$.key[4][0]"),
				JsonPath.compile("$.key[4][1]"), JsonPath.compile("$.key[5]"), JsonPath.compile("$.key[5].key"),
				JsonPath.compile("$.foo") });

		final List<JsonPath> actuals = new ArrayList<JsonPath>();

		VisitingReader visitingReader = new DummyVisitingReader();
		visitingReader.accept(new PathAwareJsonVisitor<Void>() {
			@Override
			protected JsonObjectVisitor<Void> afterVisitObject(JsonObjectVisitor<Void> visitor) {
				actuals.add(currentPath());
				return super.afterVisitObject(visitor);
			}

			@Override
			protected JsonArrayVisitor<Void> afterVisitArray(JsonArrayVisitor<Void> visitor) {
				actuals.add(currentPath());
				return super.afterVisitArray(visitor);
			}

			@Override
			protected void afterVisitValue(Boolean value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitValue(Number value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitValue(String value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitNullValue() {
				actuals.add(currentPath());
			}
		});

		assertEquals(actuals, expecteds);
	}
}
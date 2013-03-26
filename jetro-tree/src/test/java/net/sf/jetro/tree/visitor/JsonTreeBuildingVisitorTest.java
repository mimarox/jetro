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
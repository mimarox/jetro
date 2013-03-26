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
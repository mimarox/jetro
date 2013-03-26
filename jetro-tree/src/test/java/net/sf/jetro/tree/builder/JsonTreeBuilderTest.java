package net.sf.jetro.tree.builder;

import static org.testng.Assert.assertEquals;
import net.sf.jetro.tree.JsonElement;

import org.testng.annotations.Test;

public class JsonTreeBuilderTest {

	@Test
	public void shouldBuildJsonTree() {
		String json = "{\"foo\":null,\"bar\":[true,\"hello\",2]}";

		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonElement root = builder.build(json);

		String actual = root.toJson();
		assertEquals(actual, json);
	}
}
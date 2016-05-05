package net.sf.jetro.stream.visitor;

import net.sf.jetro.stream.JsonGenerator;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author oliver.burkhalter
 */
public class JsonGeneratorTest {

	@Test
	public void testStringWithDouleQuotesInValue() throws IOException {

		// Given:
		String valueIn = "Test msg \"double quotes\" testing";
		String expectedJsonValue = "Test msg \\\"double quotes\\\" testing";
		String expectedJson = "{\"key\":\"" + expectedJsonValue + "\"}";

		// When:
		StringBuffer out = new StringBuffer();
		JsonGenerator testee = new JsonGenerator(out);

		testee.beginObject();
		testee.name("key");
		testee.value(valueIn);
		testee.endObject();

		// Then:
		assertEquals(out.toString(), expectedJson);
	}
}

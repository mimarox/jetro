package net.sf.jetro.stream.visitor;

import static org.testng.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

import org.testng.annotations.Test;

public class JsonVisitorTest {

	@Test
	public void testJsonVisitor() {
		String json = "{\"key\":[1,2.0,\"<&>\",true,[\">&<\",\"bar\"],{\"key\":\"äöü\"}]}";
		String expected = "{\"key\":[1,2.0,\"&lt;&amp;&gt;\",true,[\"&gt;&amp;&lt;\",\"bar\"],{\"key\":\"&auml;&ouml;&uuml;\"}]}";

		StringReader in = new StringReader(json);
		StringWriter out = new StringWriter();

		JsonReader reader = new JsonReader(in);
		JsonWriter writer = new JsonWriter(out);

		StreamVisitingReader visitingReader = new StreamVisitingReader(reader);
		visitingReader.accept(new UniformChainedJsonVisitor<Void>(new JsonWritingVisitor(writer)) {
			@Override
			protected String beforeVisitValue(String value) {
				value = value.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
					.replaceAll("ä", "&auml;").replaceAll("ö", "&ouml;").replaceAll("ü", "&uuml;");
				return value;
			}
		});

		String actual = out.toString();
		assertEquals(actual, expected);
	}
}
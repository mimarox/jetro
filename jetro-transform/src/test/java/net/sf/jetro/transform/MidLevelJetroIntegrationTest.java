package net.sf.jetro.transform;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.transform.beans.ExpectedObject;
import net.sf.jetro.transform.beans.SourceObject;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

public class MidLevelJetroIntegrationTest {
	 private class TestTransformer extends PathAwareJsonVisitor<Void> {
		
		@Override
		protected JsonObjectVisitor<Void> afterVisitObject(final JsonObjectVisitor<Void> visitor) {
			if (currentPath().matches(JsonPath.compile("$"))) {
				visitor.visitProperty("a");
				JsonArrayVisitor<Void> arrayVisitor = visitor.visitArray();
				arrayVisitor.visitValue("b");
				arrayVisitor.visitEnd();
			}
			
			return visitor;
		}
	};
	
	@Test
	public void shouldTransformInputStreamToOutputStream() throws Exception {
		ByteArrayInputStream source = new ByteArrayInputStream("{}".getBytes("UTF-8"));
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		
		ChainedJsonVisitor<Void> transformer = new TestTransformer();
		
		Jetro.transform(source).applying(transformer).writingTo(target);
		
		String actual = target.toString("UTF-8");
		String expected = "{\"a\":[\"b\"]}";
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformStringToWriter() {
		String source = "{}";
		StringWriter target = new StringWriter();
		
		ChainedJsonVisitor<Void> transformer = new TestTransformer();
		
		Jetro.transform(source).applying(transformer).writingTo(target);

		String actual = target.toString();
		String expected = "{\"a\":[\"b\"]}";

		assertEquals(actual, expected);		
	}
	
	@Test
	public void shouldTransformJsonObjectToJsonObject() {
		JsonObject source = new JsonObject();
		ChainedJsonVisitor<Void> transformer = new TestTransformer();
		
		JsonElement actual = Jetro.transform(source).applying(transformer)
				.andReturnAsJsonElement();
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", new JsonArray(
				Arrays.asList(new JsonString("b")))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformObjectToObject() {
		SourceObject source = new SourceObject();
		ChainedJsonVisitor<Void> transformer = new TestTransformer();

		ExpectedObject actual = Jetro.transform(source).applying(transformer)
				.andReturnAsObject(ExpectedObject.class);
		
		ExpectedObject expected = new ExpectedObject();
		expected.setA(Arrays.asList("b"));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToString() {
		String expected = "[1,2,3]";
		
		String actual = Jetro.transform(expected).applyingNone().andReturnAsJson();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToInputStream()
			throws IOException {
		String source = "[1,2,3]";
		
		InputStream actual =
				Jetro.transform(source).applyingNone().andReturnAsInputStream();
		
		InputStream expected = new ByteArrayInputStream(source.getBytes("UTF-8"));
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToInputStreamWithContext()
			throws IOException {
		String source = "[1,2,3]";
		
		InputStream actual = Jetro.transform(source).applyingNone()
				.andReturnAsInputStream(new RenderContext());
		
		InputStream expected = new ByteArrayInputStream(source.getBytes("UTF-8"));
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToInputStreamWithCharsetName()
			throws IOException {
		String source = "[1,2,3]";
		
		InputStream actual = Jetro.transform(source).applyingNone()
				.andReturnAsInputStream("UTF-8");
		
		InputStream expected = new ByteArrayInputStream(source.getBytes("UTF-8"));
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToInputStreamWithContextAndCharsetName()
			throws IOException {
		String source = "[1,2,3]";
		
		InputStream actual = Jetro.transform(source).applyingNone()
				.andReturnAsInputStream(new RenderContext(), "UTF-8");
		
		InputStream expected = new ByteArrayInputStream(source.getBytes("UTF-8"));
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToReader() throws IOException {
		String source = "[1,2,3]";
		
		Reader actual =
				Jetro.transform(source).applyingNone().andReturnAsReader();
		
		Reader expected = new StringReader(source);
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
	
	@Test
	public void shouldTransformApplyingNoneStringToReaderWithContext()
			throws IOException {
		String source = "[1,2,3]";
		
		Reader actual =	Jetro.transform(source).applyingNone()
				.andReturnAsReader(new RenderContext());
		
		Reader expected = new StringReader(source);
		
		assertTrue(IOUtils.contentEquals(actual, expected));
	}
}

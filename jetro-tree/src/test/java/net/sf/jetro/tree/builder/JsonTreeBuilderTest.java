/*
 * #%L
 * Jetro Tree
 * %%
 * Copyright (C) 2013 - 2019 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.sf.jetro.tree.builder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;

import org.testng.annotations.Test;

import net.sf.jetro.stream.visitor.LazilyParsedNumber;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonBoolean;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.JsonNull;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonString;

public class JsonTreeBuilderTest {

	@Test
	public void shouldBuildJsonTree() {
		String json = "{\"foo\":null,\"bar\":[true,\"hello\",2]}";

		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonElement actual = builder.buildFrom(json);

		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("foo", new JsonNull()));
		expected.add(new JsonProperty("bar", new JsonArray(Arrays.asList(
				new JsonBoolean(true), new JsonString("hello"),
				new JsonNumber(new LazilyParsedNumber("2"))))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeWithTransformers() {
		String json = "[\"0\"]";
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonElement root = builder.buildFrom(json,
				new StringValueTransformingVisitor("1"),
				new StringValueTransformingVisitor("2"),
				new StringValueTransformingVisitor("3"));
		
		String actual = root.toJson();
		String expected = "[\"0123\"]";
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromUrl() throws Exception {
		URL url = mockURL(new ByteArrayInputStream("[0]".getBytes("UTF-8")));
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonArray actual = (JsonArray) builder.buildFrom(url);
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNumber(new LazilyParsedNumber("0"))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromUrlWithCharsetName() throws Exception {
		URL url = mockURL(new ByteArrayInputStream("[0]".getBytes("UTF-8")));
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonArray actual = (JsonArray) builder.buildFrom(url, "UTF-8");
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNumber(new LazilyParsedNumber("0"))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromUrlWithTransformers() throws Exception {
		URL url = mockURL(new ByteArrayInputStream("{\"foo\":true}".getBytes("UTF-8")));
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject actual = (JsonObject) builder.buildFrom(url,
				new RenamingPropertyVisitor());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("renamed", true));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromUrlWithCharsetNameAndTransformers()
			throws Exception {
		URL url = mockURL(new ByteArrayInputStream("{\"foo\":true}".getBytes("UTF-8")));
		
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject actual = (JsonObject) builder.buildFrom(url, "UTF-8",
				new RenamingPropertyVisitor());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("renamed", true));
		
		assertEquals(actual, expected);
	}
	
	private URL mockURL(final InputStream stream) throws Exception {
		final URLConnection mockUrlCon = mock(URLConnection.class);
		when(mockUrlCon.getInputStream()).thenReturn(stream);

		//make getLastModified() return first 10, then 11
		when(mockUrlCon.getLastModified()).thenReturn((Long)10L, (Long)11L);

		URLStreamHandler stubUrlHandler = new URLStreamHandler() {
		    @Override
		     protected URLConnection openConnection(URL u) throws IOException {
		        return mockUrlCon;
		     }            
		};
		
		return new URL("foo", "bar", 99, "/foobar", stubUrlHandler);
	}

	@Test
	public void shouldBuildJsonTreeFromInputStream() throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonArray actual = (JsonArray) builder.buildFrom(
				new ByteArrayInputStream("[0]".getBytes("UTF-8")));
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNumber(new LazilyParsedNumber("0"))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromInputStreamWithCharsetName() throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonArray actual = (JsonArray) builder.buildFrom(
				new ByteArrayInputStream("[0]".getBytes("UTF-8")), "UTF-8");
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNumber(new LazilyParsedNumber("0"))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromInputStreamWithTransformers() throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject actual = (JsonObject) builder.buildFrom(
				new ByteArrayInputStream("{\"foo\":true}".getBytes("UTF-8")),
				new RenamingPropertyVisitor());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("renamed", true));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromInpuStreamWithCharsetNameAndTransformers()
			throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject actual = (JsonObject) builder.buildFrom(
				new ByteArrayInputStream("{\"foo\":true}".getBytes("UTF-8")), "UTF-8",
				new RenamingPropertyVisitor());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("renamed", true));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromReader() throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonArray actual = (JsonArray) builder.buildFrom(
				new StringReader("[0]"));
		
		JsonArray expected = new JsonArray(Arrays.asList(
				new JsonNumber(new LazilyParsedNumber("0"))));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldBuildJsonTreeFromReaderWithTransformers() throws Exception {
		JsonTreeBuilder builder = new JsonTreeBuilder();
		JsonObject actual = (JsonObject) builder.buildFrom(
				new StringReader("{\"foo\":true}"),
				new RenamingPropertyVisitor());
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("renamed", true));
		
		assertEquals(actual, expected);
	}
}

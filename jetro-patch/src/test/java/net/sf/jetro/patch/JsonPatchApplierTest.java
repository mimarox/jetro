/*
 * #%L
 * Jetro Patch
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
package net.sf.jetro.patch;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.testng.annotations.Test;

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonNumber;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;

@Test(groups = "individualTests")
public class JsonPatchApplierTest {
	private JsonPatchApplier getJsonPatchApplier() {
		JsonObject source = new JsonObject();
		
		JsonArray value = new JsonArray();
		value.add(new JsonNumber(1));
		
		JsonObject patchOperation = new JsonObject();
		patchOperation.add(new JsonProperty("op", "add"));
		patchOperation.add(new JsonProperty("path", "/a"));
		patchOperation.add(new JsonProperty("value", value));
		
		return new JsonPatchApplier(source, patchOperation);
	}
	
	@Test
	public void shouldAndReturnAsJson() throws JsonPatchException {
		String expected = "{\"a\":[1]}";
		String actual = getJsonPatchApplier().andReturnAsJson();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsJsonWithRenderer() throws JsonPatchException {
		String expected = "{\"a\":[1]}";
		String actual = getJsonPatchApplier().andReturnAsJson(new DefaultJsonRenderer());
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndWriteResultToOutputStream() throws JsonPatchException, IOException {
		String expected = "{\"a\":[1]}";

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getJsonPatchApplier().andWriteResultTo(out);
		
		String actual = out.toString("UTF-8");
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndWriteResultToWriter() throws JsonPatchException {
		String expected = "{\"a\":[1]}";

		StringWriter writer = new StringWriter();
		getJsonPatchApplier().andWriteResultTo(writer);
		
		String actual = writer.toString();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsJsonType() throws JsonPatchException {
		JsonArray value = new JsonArray();
		value.add(new JsonNumber(1));
		
		JsonObject expected = new JsonObject();
		expected.add(new JsonProperty("a", value));
		
		JsonType actual = getJsonPatchApplier().andReturnAsJsonType();
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsObjectWithClass() throws JsonPatchException {
		TestBean expected = new TestBean();
		expected.setA(Arrays.asList(1));
		
		TestBean actual = getJsonPatchApplier().andReturnAsObject(TestBean.class);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsObjectWithTypeToken() throws JsonPatchException {
		TestBean expected = new TestBean();
		expected.setA(Arrays.asList(1));
		
		TestBean actual = getJsonPatchApplier().andReturnAsObject(TypeToken.of(TestBean.class));
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsObjectWithClassWithContext() throws JsonPatchException {
		TestBean expected = new TestBean();
		expected.setA(Arrays.asList(1));
		
		DeserializationContext context = DeserializationContext.getDefault();
		
		TestBean actual = getJsonPatchApplier().andReturnAsObject(TestBean.class, context);
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAndReturnAsObjectWithTypeTokenWithContext() throws JsonPatchException {
		TestBean expected = new TestBean();
		expected.setA(Arrays.asList(1));
		
		DeserializationContext context = DeserializationContext.getDefault();
		
		TestBean actual = getJsonPatchApplier().andReturnAsObject(TypeToken.of(TestBean.class),
				context);
		
		assertEquals(actual, expected);
	}
}

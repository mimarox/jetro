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

import static net.sf.jetro.patch.JsonPatch.patch;
import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.tree.JsonObject;

@Test(groups = "individualTests")
public class JsonPatchTest {
	
	@Test
	public void shouldPatchString() {
		JsonSourceCollector collector = patch("{}");
		assertNotNull(collector);
	}
	
	@Test
	public void shouldPatchInputStream() throws IOException {
		JsonSourceCollector collector = patch(new ByteArrayInputStream("{}".getBytes("UTF-8")));
		assertNotNull(collector);
	}
	
	@Test
	public void shouldPatchReader() {
		JsonSourceCollector collector = patch(new StringReader("[]"));
		assertNotNull(collector);
	}
	
	@Test
	public void shouldPatchJsonType() {
		JsonSourceCollector collector = patch(new JsonObject());
		assertNotNull(collector);
	}
	
	@Test
	public void shouldPatchObject() {
		JsonSourceCollector collector = patch(new ArrayList<>());
		assertNotNull(collector);
	}
	
	@Test
	public void shouldPatchObjectWithContext() {
		JsonSourceCollector collector = patch(new ArrayList<>(), new SerializationContext());
		assertNotNull(collector);
	}
}

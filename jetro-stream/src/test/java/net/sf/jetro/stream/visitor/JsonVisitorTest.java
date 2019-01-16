/*
 * #%L
 * Jetro Stream
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
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
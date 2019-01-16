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
package net.sf.jetro.stream.transformations;

import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import org.testng.annotations.Test;

import java.io.StringReader;

/**
 * Created by matthias.rothe on 20.02.14.
 */
public class ReplacingTransformationsOnStreamTest {
	private JsonReturningVisitor writer = new JsonReturningVisitor();
	private ChainedJsonVisitor<String> transformer = new UniformChainedJsonVisitor<String>(writer) {
		@Override
		protected String beforeVisitValue(String value) {
//				JsonPath currentPath = currentPath();

//				if (currentPath.matches(JsonPath.compile("$.favorites[*]"))) {
			JsonObjectVisitor objectVisitor = getNextVisitor().visitObject();
			objectVisitor.visitProperty("path");
			objectVisitor.visitValue(value);
			objectVisitor.visitProperty("title");
			objectVisitor.visitValue("Title for: " + value);
			objectVisitor.visitEnd();

			return null;
//				}

//				return value;
		}
	};

	public static void main(String[] args) throws InterruptedException {
		ReplacingTransformationsOnStreamTest test = new ReplacingTransformationsOnStreamTest();

		for (int i = 0; i < 50; i++) {
			test.runPerformanceTest();
		}

		Thread.sleep(1000 * 60 * 60 * 24);
	}

	private void runPerformanceTest() {
		long start = System.currentTimeMillis();

		for (int i = 0; i < 1000000; i++) {
			performTransformations(
					"{" +
							"\"favorites\": [" +
							"\"/content/page/1\", " +
							"\"/content/page/2\", " +
							"\"/content/page/3\"" +
							"]" +
							"}");
		}

		long end = System.currentTimeMillis();

		System.out.println("Dauer für 1'000'000 Durchläufe: " + (end - start) + "ms");
	}

	@Test
	public void testReplacingTransformations() {
		String out = performTransformations(
				"{" +
						"\"favorites\": [" +
						"\"/content/page/1\", " +
						"\"/content/page/2\", " +
						"\"/content/page/3\"" +
						"]" +
						"}");
		System.out.println(out);
	}

	private String performTransformations(String json) {
		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));
		reader.accept(transformer);
		return writer.getVisitingResult();
	}
}

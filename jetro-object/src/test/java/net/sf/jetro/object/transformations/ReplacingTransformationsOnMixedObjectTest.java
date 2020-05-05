/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object.transformations;

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.transformations.beans.Favorite;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class ReplacingTransformationsOnMixedObjectTest {

	public static void main(String[] args) {
		ReplacingTransformationsOnMixedObjectTest test =
				new ReplacingTransformationsOnMixedObjectTest();

		for (int i = 0; i < 10; i++) {
			test.runPerformanceTest();
		}
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
		final ObjectMapper mapper = new ObjectMapper();

		StreamVisitingReader reader = new StreamVisitingReader(new JsonReader(new StringReader(json)));

		JsonReturningVisitor writer = new JsonReturningVisitor();

		ChainedJsonVisitor<String> transformer = new UniformChainedJsonVisitor<String>(writer) {
			@Override
			protected String beforeVisitValue(String value) {
				Favorite favorite = new Favorite();
				favorite.setPage(value);
				favorite.setTitle("Title for: " + value);

				mapper.merge(favorite).into(getNextVisitor());

				return null;
			}
		};

		try {
			reader.accept(transformer);
			return writer.getVisitingResult();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
}

/*
 * #%L
 * Jetro Core
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
package net.sf.jetro.visitor.pathaware;

import static org.testng.Assert.assertEquals;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.VisitingReader;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathAwareJsonVisitorTest {

	@Test
	public void shouldProduceCorrectPaths() {
		List<JsonPath> expecteds = Arrays.asList(new JsonPath[] { new JsonPath(), JsonPath.compile("$.key"),
				JsonPath.compile("$.key[0]"), JsonPath.compile("$.key[1]"), JsonPath.compile("$.key[2]"),
				JsonPath.compile("$.key[3]"), JsonPath.compile("$.key[4]"), JsonPath.compile("$.key[4][0]"),
				JsonPath.compile("$.key[4][1]"), JsonPath.compile("$.key[5]"), JsonPath.compile("$.key[5].key"),
				JsonPath.compile("$.foo") });

		final List<JsonPath> actuals = new ArrayList<JsonPath>();

		VisitingReader visitingReader = new DummyVisitingReader();
		visitingReader.accept(new PathAwareJsonVisitor<Void>() {
			@Override
			protected JsonObjectVisitor<Void> afterVisitObject(JsonObjectVisitor<Void> visitor) {
				actuals.add(currentPath());
				return super.afterVisitObject(visitor);
			}

			@Override
			protected JsonArrayVisitor<Void> afterVisitArray(JsonArrayVisitor<Void> visitor) {
				actuals.add(currentPath());
				return super.afterVisitArray(visitor);
			}

			@Override
			protected void afterVisitValue(Boolean value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitValue(Number value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitValue(String value) {
				actuals.add(currentPath());
			}

			@Override
			protected void afterVisitNullValue() {
				actuals.add(currentPath());
			}
		});

		assertEquals(actuals, expecteds);
	}
}
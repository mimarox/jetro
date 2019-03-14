/*
 * #%L
 * Jetro JsonPath
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
package net.sf.jetro.path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

public class JsonPathTest {
	private JsonPath jsonPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
			new ArrayIndexPathElement(true, false), new PropertyNamePathElement(true, false), new ArrayIndexPathElement(1) }, false);

	@Test
	public void shouldOutputPathAsString() {
		String expected = "$.foo[*].*[1]";
		String actual = jsonPath.toString();

		assertEquals(actual, expected);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldBeImmutableWhenAppending() {
		JsonPath newPropertyPath = jsonPath.append(new PropertyNamePathElement("bar"));

		String expected = "$.foo[*].*[1].bar";
		String actual = newPropertyPath.toString();

		assertNotSame(newPropertyPath, jsonPath);
		assertEquals(actual, expected);
		assertNotEquals(actual, jsonPath.toString());

		JsonPath newArrayPath = newPropertyPath.append(new ArrayIndexPathElement(2));

		expected += "[2]";
		actual = newArrayPath.toString();

		assertNotSame(newArrayPath, jsonPath);
		assertNotSame(newArrayPath, newPropertyPath);
		assertEquals(actual, expected);
		assertNotEquals(actual, jsonPath.toString());
		assertNotEquals(actual, newPropertyPath.toString());
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldBeImmutableWhenReplacing() {
		JsonPath newPropertyPath = jsonPath.replaceLastElementWith(new PropertyNamePathElement("bar"));

		String expected = "$.foo[*].*.bar";
		String actual = newPropertyPath.toString();

		assertNotSame(newPropertyPath, jsonPath);
		assertEquals(actual, expected);
		assertNotEquals(actual, jsonPath.toString());

		JsonPath newArrayPath = newPropertyPath.replaceLastElementWith(new ArrayIndexPathElement(2));

		expected = "$.foo[*].*[2]";
		actual = newArrayPath.toString();

		assertNotSame(newArrayPath, jsonPath);
		assertNotSame(newArrayPath, newPropertyPath);
		assertEquals(actual, expected);
		assertNotEquals(actual, jsonPath.toString());
		assertNotEquals(actual, newPropertyPath.toString());
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldBeImmutableWhenRemoving() {
		JsonPath removedElementPath = jsonPath.removeLastElement();

		String expected = "$.foo[*].*";
		String actual = removedElementPath.toString();

		assertNotSame(removedElementPath, jsonPath);
		assertEquals(actual, expected);
		assertNotEquals(actual, jsonPath.toString());
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldMatch() {
		assertTrue(jsonPath.matches(jsonPath), "JsonPath should match itself, but doesn't");

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) }, false);
		assertTrue(matchingPath.matches(jsonPath), matchingPath + " doesn't match " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldMatchWithMatchesAllFurther() {
		JsonPath pathPattern = jsonPath.append(new MatchesAllFurtherPathElement());

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) }, false);
		assertTrue(matchingPath.matches(pathPattern), matchingPath + " doesn't match " + pathPattern);

		matchingPath = matchingPath.append(new PropertyNamePathElement(true, false)).append(new ArrayIndexPathElement(2));
		assertTrue(matchingPath.matches(pathPattern), matchingPath + " doesn't match " + pathPattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchTooShort() {
		assertFalse(new JsonPath(new JsonPathElement[] {}, false).matches(jsonPath), "empty path matches " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchTooShortWithMatchesAllFurther() {
		JsonPath pathPattern = jsonPath.append(new MatchesAllFurtherPathElement());
		assertFalse(new JsonPath(new JsonPathElement[] {}, false).matches(pathPattern), "empty path matches " + pathPattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchTooLong() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1),
				new PropertyNamePathElement("zoom") }, false);

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchWrongType() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new PropertyNamePathElement("zoom") }, false);

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchWrongPropertyName() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("baz"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) }, false);

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchWrongArrayIndex() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(2) }, false);

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldMatchOptionalSkipped() {
		JsonPath pattern = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(true, true), new PropertyNamePathElement("bar") }, true);

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new PropertyNamePathElement("bar") }, false);

		assertTrue(matchingPath.matches(pattern), matchingPath + " doesn't match " + pattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldMatchOptionalValueApplied() {
		JsonPath pattern = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1, true), new PropertyNamePathElement("bar") }, true);

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1, false), new PropertyNamePathElement("bar") }, false);

		assertTrue(matchingPath.matches(pattern), matchingPath + " doesn't match " + pattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotMatchOptionalValueApplied() {
		JsonPath pattern = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1, true), new PropertyNamePathElement("bar") }, true);

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2, false), new PropertyNamePathElement("bar") }, false);

		assertFalse(matchingPath.matches(pattern), matchingPath + " matches " + pattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldMatchOptionalWildcardApplied() {
		JsonPath pattern = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(true, true), new PropertyNamePathElement("bar") }, true);

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1, false), new PropertyNamePathElement("bar") }, false);

		assertTrue(matchingPath.matches(pattern), matchingPath + " doesn't match " + pattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldNotRootPathMatch() {
		JsonPath pattern = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1, true), new PropertyNamePathElement("bar") }, true);

		JsonPath matchingPath = new JsonPath();

		assertFalse(matchingPath.matches(pattern), matchingPath + " matches " + pattern);
	}

	@Test(dependsOnMethods = "shouldOutputPathAsString")
	public void shouldCompile() {
		String expected = "$.*[*].foo.bar[1][2].baz";
		String actual = JsonPath.compile(expected).toString();

		assertEquals(actual, expected);
	}

	@Test
	public void shouldReturnCorrectDepth() {
		assertEquals(jsonPath.getDepth(), 4);
		assertEquals(jsonPath.append(new PropertyNamePathElement("bar")).getDepth(), 5);
		assertEquals(jsonPath.removeLastElement().getDepth(), 3);
	}

	@Test(dependsOnMethods = "shouldCompile")
	@DataBinding(propertiesPrefix = "parentPath")
	public void shouldBeParentPath(@TestInput(name = "parentPath") final String parentPath,
			@TestInput(name = "childPath") final String childPath) {
		assertTrue(JsonPath.compile(parentPath).isParentPathOf(JsonPath.compile(childPath)), parentPath
				+ " should have been parent path of " + childPath);
	}

	@Test(dependsOnMethods = "shouldCompile")
	@DataBinding(propertiesPrefix = "notParentPath")
	public void shouldNotBeParentPath(@TestInput(name = "jsonPath") final String jsonPath,
			@TestInput(name = "notChildPath") final String notChildPath) {
		assertFalse(JsonPath.compile(jsonPath).isParentPathOf(JsonPath.compile(notChildPath)), jsonPath
				+ " should not have been parent path of " + notChildPath);
	}
	
	@Test
	public void shouldHaveConsistentEqualsAndHashcode() {
		JsonPath firstPath = JsonPath.compile("$[0]");
		JsonPath secondPath = JsonPath.compile("$[0][0]").removeLastElement();
		
		assertEquals(firstPath, secondPath);
		
		Set<JsonPath> paths = new HashSet<>();
		paths.add(firstPath);
		
		assertTrue(paths.contains(secondPath));
	}
	
	@Test
	public void shouldNotContainOptionalsOnLastElementRemoval() {
		JsonPath path = JsonPath.compile("$.foo.optional?");
		
		assertTrue(path.containsOptionals());
		
		path = path.removeLastElement().removeLastElement();
		
		assertFalse(path.containsOptionals());
	}
	
	@Test
	public void shouldNotContainOptionalsOnLastElementReplacement() {
		JsonPath path = JsonPath.compile("$.foo.optional?");
		
		assertTrue(path.containsOptionals());
		
		path = path.removeLastElement().replaceLastElementWith(
				new PropertyNamePathElement("required"));
		
		assertFalse(path.containsOptionals());
	}
}
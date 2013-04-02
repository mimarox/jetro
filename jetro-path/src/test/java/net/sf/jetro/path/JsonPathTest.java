package net.sf.jetro.path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;
import net.sf.testng.databinding.DataBinding;
import net.sf.testng.databinding.TestInput;

import org.testng.annotations.Test;

public class JsonPathTest {
	private JsonPath jsonPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
			new ArrayIndexPathElement(true), new PropertyNamePathElement(true), new ArrayIndexPathElement(1) });

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

	@Test
	public void shouldMatch() {
		assertTrue(jsonPath.matches(jsonPath), "JsonPath should match itself, but doesn't");

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) });
		assertTrue(matchingPath.matches(jsonPath), matchingPath + " doesn't match " + jsonPath);
	}

	@Test
	public void shouldMatchWithMatchesAllFurther() {
		JsonPath pathPattern = jsonPath.append(new MatchesAllFurtherPathElement());

		JsonPath matchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) });
		assertTrue(matchingPath.matches(pathPattern), matchingPath + " doesn't match " + pathPattern);

		matchingPath = matchingPath.append(new PropertyNamePathElement(true)).append(new ArrayIndexPathElement(2));
		assertTrue(matchingPath.matches(pathPattern), matchingPath + " doesn't match " + pathPattern);
	}

	@Test
	public void shouldNotMatchTooShort() {
		assertFalse(new JsonPath(new JsonPathElement[] {}).matches(jsonPath), "empty path matches " + jsonPath);
	}

	@Test
	public void shouldNotMatchTooShortWithMatchesAllFurther() {
		JsonPath pathPattern = jsonPath.append(new MatchesAllFurtherPathElement());
		assertFalse(new JsonPath(new JsonPathElement[] {}).matches(pathPattern), "empty path matches " + pathPattern);
	}

	@Test
	public void shouldNotMatchTooLong() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(2), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1),
				new PropertyNamePathElement("zoom") });

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test
	public void shouldNotMatchWrongType() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new PropertyNamePathElement("zoom") });

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test
	public void shouldNotMatchWrongPropertyName() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("baz"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(1) });

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
	}

	@Test
	public void shouldNotMatchWrongArrayIndex() {
		JsonPath nonMatchingPath = new JsonPath(new JsonPathElement[] { new PropertyNamePathElement("foo"),
				new ArrayIndexPathElement(1), new PropertyNamePathElement("bar"), new ArrayIndexPathElement(2) });

		assertFalse(nonMatchingPath.matches(jsonPath), nonMatchingPath + " matches " + jsonPath);
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
}
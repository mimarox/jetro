package net.sf.jetro.transform.highlevel;

import java.util.function.Predicate;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonType;

public class ReplaceIfSpecificationTest {
	private static final JsonPath ROOT_PATH = JsonPath.compile("$");
	
	private static final TransformationSpecification SPECIFICATION =
			new TransformationSpecification() {
				
				@Override
				protected void specify() {
					throw new UnsupportedOperationException();
				}
			};

	private static final Predicate<JsonType> PREDICATE = json -> true;
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullJsonPath() {
		new ReplaceIfSpecification(null, PREDICATE, SPECIFICATION);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "predicate must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullPredicate() {
		new ReplaceIfSpecification(ROOT_PATH, null, SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullSpecification() {
		new ReplaceIfSpecification(ROOT_PATH, PREDICATE, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionWithNullObjectNullContext() {
		new ReplaceIfSpecification(ROOT_PATH, PREDICATE, SPECIFICATION)
		.with(null, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionWithFromVariableNullVariableName() {
		new ReplaceIfSpecification(ROOT_PATH, PREDICATE, SPECIFICATION)
		.withFromVariable(null);
	}
}

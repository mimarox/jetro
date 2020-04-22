package net.sf.jetro.transform.highlevel;

import org.testng.annotations.Test;

public class RenameSpecificationTest {
	private static final TransformationSpecification SPECIFICATION =
			new TransformationSpecification() {
		
		@Override
		protected void specify() {
			throw new UnsupportedOperationException();
		}
	};

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "name must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullName() {
		new RenameSpecification(null, SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullSpecification() {
		new RenameSpecification("", null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "name must not be empty",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceEmptyName() {
		new RenameSpecification("", SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "name must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionIgnoringCaseNullName() {
		RenameSpecification.ignoringCase(null, SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionIgnoringCaseNullSpecification() {
		RenameSpecification.ignoringCase("", null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "name must not be empty",
			groups = "negativeTests")
	public void shouldThrowExceptionIgnoringCaseEmptyName() {
		RenameSpecification.ignoringCase("", SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "pattern must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionMatchingNullPattern() {
		RenameSpecification.matching(null, SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionMatchingNullSpecification() {
		RenameSpecification.matching("", null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "pattern must not be empty",
			groups = "negativeTests")
	public void shouldThrowExceptionMatchingEmptyPattern() {
		RenameSpecification.matching("", SPECIFICATION);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "newName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionToNullNewName() {
		new RenameSpecification("name", SPECIFICATION).to(null);;
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp = "newName must not be empty",
			groups = "negativeTests")
	public void shouldThrowExceptionToEmptyNewName() {
		new RenameSpecification("name", SPECIFICATION).to("");;
	}
}

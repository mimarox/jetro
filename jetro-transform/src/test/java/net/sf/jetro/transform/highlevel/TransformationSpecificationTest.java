package net.sf.jetro.transform.highlevel;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class TransformationSpecificationTest {	
	private static final TransformationSpecification SPECIFICATION =
			new TransformationSpecification() {
				
				@Override
				protected void specify() {
					throw new UnsupportedOperationException();
				}
			};
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAtNullJsonPathString() {
		SPECIFICATION.at((String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAtNullJsonPath() {
		SPECIFICATION.at((JsonPath) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionCaptureNullJsonPathString() {
		SPECIFICATION.capture((String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionCaptureNullJsonPath() {
		SPECIFICATION.capture((JsonPath) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionKeepNullJsonPathString() {
		SPECIFICATION.keep((String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionKeepNullJsonPath() {
		SPECIFICATION.keep((JsonPath) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRemoveNullJsonPathString() {
		SPECIFICATION.remove((String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "path must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRemoveNullJsonPath() {
		SPECIFICATION.remove((JsonPath) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "name must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamePropertyNullName() {
		SPECIFICATION.renameProperties(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "pattern must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamePropertiesMatchingNullPattern() {
		SPECIFICATION.renamePropertiesMatching(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "name must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamePropertyIgnoreCaseNullName() {
		SPECIFICATION.renamePropertiesIgnoringCase(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "visitor must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionApplyingCustomNullVisitor() {
		SPECIFICATION.applyCustomVisitor(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionApplyingNullSpecification() {
		SPECIFICATION.applySpecification(null);
	}	
}

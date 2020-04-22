package net.sf.jetro.transform.highlevel;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;

public class CaptureSpecificationTest {
	private static final JsonPath ROOT_PATH = JsonPath.compile("$");
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
	public void shouldThrowExceptionNewInstanceNullJsonPath() {
		new CaptureSpecification(null, SPECIFICATION);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullSpecification() {
		new CaptureSpecification(ROOT_PATH, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAndSaveAsNullVariableName() {
		new CaptureSpecification(ROOT_PATH, SPECIFICATION).andSaveAs(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "editor must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionEditNullEditor() {
		new CaptureSpecification(ROOT_PATH, SPECIFICATION).edit(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "editor must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionEditAndReplaceNullEditor() {
		new CaptureSpecification(ROOT_PATH, SPECIFICATION).editAndReplace(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "editor must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionEditEachNullEditor() {
		new CaptureSpecification(ROOT_PATH, SPECIFICATION).editEach(null);
	}
}

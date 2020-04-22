package net.sf.jetro.transform.highlevel;

import java.util.function.Function;

import org.testng.annotations.Test;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonType;

public class CaptureEditSpecificationTest {
	private static final JsonPath ROOT_PATH = JsonPath.compile("$");
	private static final Function<JsonType, JsonType> EDITOR = Function.identity();
	
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
		new CaptureEditSpecification<>(null, EDITOR, SPECIFICATION);		
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "editor must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullEditor() {
		new CaptureEditSpecification<>(ROOT_PATH, null, SPECIFICATION);		
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNewInstanceNullSpecification() {
		new CaptureEditSpecification<>(ROOT_PATH, EDITOR, null);		
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAndSaveAsNullVariableName() {
		new CaptureEditSpecification<>(ROOT_PATH, EDITOR, SPECIFICATION)
		.andSaveAs(null);
	}
}

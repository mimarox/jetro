package net.sf.jetro.transform.highlevel;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonType;

public class PathAwareSpecificationTest {
	private static final JsonPath PATH = JsonPath.compile("$");
	private static final TransformationSpecification SPECIFICATION =
			new TransformationSpecification() {
				
				@Override
				protected void specify() {
					throw new UnsupportedOperationException();
				}
			};
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyBooleanValue() {
		new PathAwareSpecification(PATH, SPECIFICATION).addJsonProperty(null, true);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyNumberValue() {
		new PathAwareSpecification(PATH, SPECIFICATION).addJsonProperty(null, 1);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyStringValue() {
		new PathAwareSpecification(PATH, SPECIFICATION).addJsonProperty(null, "value");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyJsonTypeValue() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addJsonProperty(null, new JsonObject());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyObjectValue() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addJsonProperty(null, new Object());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "jsonObject must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullJsonObject() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonProperties((JsonObject) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "properties must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullMap() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonProperties((Map<String, String>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "properties must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullMapNotNullContext() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonProperties((Map<String, String>) null,
				new SerializationContext());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNotNullMapNullContext() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonProperties(new HashMap<String, String>(), null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingFromNullVariableName() {
		new PathAwareSpecification(PATH, SPECIFICATION).addFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllFromNullVariableName() {
		new PathAwareSpecification(PATH, SPECIFICATION).addAllFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyFromNullKeyVariableName() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addJsonPropertyFromVariable(null, "name");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyFromKeyNullVariableName() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addJsonPropertyFromVariable("key", null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesFromNullVariableName() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonPropertiesFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")	
	public void shouldThrowExceptionAddingAllJsonTypesNullIterable() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonTypes((Iterable<JsonType>) null);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullSerializationContextObject() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonValues(null, (Object) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullSerializationContextIterable() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonValues(null, (Iterable<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesSerializationContextNullIterable() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonValues(new SerializationContext(), (Iterable<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullIterable() {
		new PathAwareSpecification(PATH, SPECIFICATION)
		.addAllJsonValues((Iterable<?>) null);
	}	
}

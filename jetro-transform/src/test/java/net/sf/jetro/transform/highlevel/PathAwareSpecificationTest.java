package net.sf.jetro.transform.highlevel;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonType;

public class PathAwareSpecificationTest {
	private static final JsonPath ROOT_PATH = JsonPath.compile("$");
	private static final JsonPath ARRAY_WILDCARD_PATH = JsonPath.compile("$[*]");
	
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
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonProperty(null, true);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyNumberValue() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonProperty(null, 1);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyStringValue() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonProperty(null, "value");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyJsonTypeValue() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonProperty(null, new JsonObject());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyNullKeyObjectValue() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonProperty(null, new Object());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "jsonObject must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullJsonObject() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonProperties((JsonObject) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "properties must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullMap() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonProperties((Map<String, String>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "properties must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNullMapNotNullContext() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonProperties((Map<String, String>) null,
				new SerializationContext());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesNotNullMapNullContext() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonProperties(new HashMap<String, String>(), null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingFromNullVariableName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION).addFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllFromNullVariableName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION).addAllFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "key must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyFromNullKeyVariableName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonPropertyFromVariable(null, "name");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonPropertyFromKeyNullVariableName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonPropertyFromVariable("key", null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "variableName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonPropertiesFromNullVariableName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonPropertiesFromVariable(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")	
	public void shouldThrowExceptionAddingAllJsonTypesNullIterable() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonTypes((Iterable<JsonType>) null);
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullSerializationContextObject() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonValues(null, (Object) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullSerializationContextIterable() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonValues(null, (Iterable<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesSerializationContextNullIterable() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonValues(new SerializationContext(), (Iterable<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "values must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingAllJsonValuesNullIterable() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addAllJsonValues((Iterable<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionAddingJsonValueObjectNullSerializationContext() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.addJsonValue(null, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "newName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamingPropertyToNullNewName() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.renamePropertyTo(null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp =
			"path must end in a property name to be renamed",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamingPropertyToAtIllegalPathRootPath() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.renamePropertyTo("name");
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class,
			expectedExceptionsMessageRegExp =
			"path must end in a property name to be renamed",
			groups = "negativeTests")
	public void shouldThrowExceptionRenamingPropertyToAtIllegalPathArrayPath() {
		new PathAwareSpecification(ARRAY_WILDCARD_PATH, SPECIFICATION)
		.renamePropertyTo("name");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionReplacingWithObjectNullSerializationContext() {
		new PathAwareSpecification(ROOT_PATH, SPECIFICATION)
		.replaceWith(null, null);
	}
}

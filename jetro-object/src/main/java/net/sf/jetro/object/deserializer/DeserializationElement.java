package net.sf.jetro.object.deserializer;

import java.util.Objects;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.path.JsonPath;

public class DeserializationElement {
	public static enum ElementType {
		ARRAY,
		OBJECT,
		PRIMITIVE
	}
	
	private final TypeToken<?> typeToken;
	private final String parentField;
	private final boolean skippedProperty;
	private final JsonPath jsonPath;
	private Object instance;
	private ElementType elementType;
	
	public DeserializationElement(TypeToken<?> typeToken) {
		this(typeToken, null, null);
	}
	
	public DeserializationElement(TypeToken<?> typeToken, Object instance) {
		this(typeToken, instance, null);
	}
	
	public DeserializationElement(TypeToken<?> typeToken, String parentField) {
		this(typeToken, null, parentField);
	}
	
	public DeserializationElement(TypeToken<?> typeToken, Object instance, String parentField) {
		Objects.requireNonNull(typeToken, "Argument typeToken must not be null.");
		
		this.typeToken = typeToken;
		this.instance = instance;
		this.parentField = parentField;
		this.skippedProperty = false;
		this.jsonPath = null;
	}

	private DeserializationElement(boolean skippedProperty, JsonPath jsonPath) {
		this.typeToken = null;
		this.parentField = null;
		this.skippedProperty = skippedProperty;
		this.jsonPath = jsonPath;
	}
	
	public static DeserializationElement skippedProperty(JsonPath jsonPath) {
		return new DeserializationElement(true, jsonPath);
	}
	
	public TypeToken<?> getTypeToken() {
		if (skippedProperty) {
			throw new IllegalStateException("Can't get a typeToken on a skippedProperty");
		}
		
		return typeToken;
	}
	
	public String getParentField() {
		if (skippedProperty) {
			throw new IllegalStateException("Can't get a parentField on a skippedProperty");
		}
		
		return parentField;
	}
	
	public Object getInstance() {
		if (skippedProperty) {
			throw new IllegalStateException("Can't get an instance on a skippedProperty");
		}
		
		return instance;
	}
	
	public void setInstance(Object instance) {
		if (skippedProperty) {
			throw new IllegalStateException("Can't set an instance on a skippedProperty");
		}
		
		this.instance = instance;
	}
	
	public boolean isProcessedProperty() {
		return !skippedProperty;
	}

	public JsonPath getJsonPath() {
		if (isProcessedProperty()) {
			throw new IllegalStateException("jsonPath is not tracked for processed properties");
		}
		
		return jsonPath;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}
}

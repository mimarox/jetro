/*
 * #%L
 * Jetro Object
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
package net.sf.jetro.object.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

import net.sf.jetro.exception.MalformedJsonException;
import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.deserializer.DeserializationElement;
import net.sf.jetro.object.deserializer.DeserializationElement.ElementType;
import net.sf.jetro.object.exception.DeserializationException;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.util.Stack;
import net.sf.jetro.visitor.pathaware.PathAwareJsonVisitor;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class ObjectBuildingVisitor<R> extends PathAwareJsonVisitor<R> {
	private final DeserializationContext context;
	private final Stack<DeserializationElement> elements = new Stack<>();

	public ObjectBuildingVisitor(TypeToken<R> targetTypeToken, DeserializationContext context) {
		Objects.requireNonNull(targetTypeToken, "Argument targetTypeToken must not be null!");
		Objects.requireNonNull(context, "Argument context must not be null!");

		this.context = context;

		elements.push(new DeserializationElement(targetTypeToken));
	}

	@Override
	protected boolean doBeforeVisitObject() {
		DeserializationElement top = elements.peek();

		if (top.isProcessedProperty()) {
			if (isList(top.getInstance())) {
				TypeToken<?> typeToken = getListMemberTypeToken(top.getTypeToken());
				Object instance = context.createInstanceOf(typeToken);
				DeserializationElement member = new DeserializationElement(typeToken, instance);
				elements.push(member);
			} else {
				Object instance = context.createInstanceOf(top.getTypeToken());
				top.setInstance(instance);
			}
	
			return true;
		} else {
			return false;
		}
	}

	private TypeToken<?> getListMemberTypeToken(TypeToken<?> typeToken) {
		return TypeToken.of(((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0]);
	}

	@Override
	protected void afterVisitObjectEnd() {
		if (elements.size() > 1) {
			DeserializationElement top = elements.pop();

			if(top.isProcessedProperty()) {
				if (!isObject(top.getInstance())) {
					throw new MalformedJsonException(
							"Object end out of scope. Expected Java Bean, but was " + top.getTypeToken().getType());
				}
	
				afterVisitValue(top);
			}
		}
	}

	private boolean isObject(Object object) {
		return !isList(object);
	}

	private boolean isList(Object object) {
		return object instanceof List;
	}

	@Override
	protected boolean doBeforeVisitArray() {
		DeserializationElement top = elements.peek();

		if (top.isProcessedProperty()) {
			if (isList(top.getInstance())) {
				TypeToken<?> typeToken = getListMemberTypeToken(top.getTypeToken());
				Object instance = context.createInstanceOf(typeToken);
				DeserializationElement member = new DeserializationElement(typeToken, instance);
				elements.push(member);
			} else {
				Object instance = context.createInstanceOf(top.getTypeToken());
				top.setInstance(instance);
			}
			
			return true;
		} else {
			JsonPath currentPath = currentPath();
			JsonPath topPath = top.getJsonPath();
			
			if (currentPath.isChildPathOf(topPath) && !currentPath.equals(topPath)) {
				elements.push(DeserializationElement.skippedProperty(topPath));
			}
			
			elements.peek().setElementType(ElementType.ARRAY);
			return false;
		}
	}

	@Override
	protected void afterVisitArrayEnd() {
		if (elements.size() > 1) {
			DeserializationElement top = elements.pop();
			
			if (top.isProcessedProperty()) {
				if (!isList(top.getInstance())) {
					throw new MalformedJsonException(
							"Array end out of scope. Expected List, but was " +
									top.getTypeToken().getType());
				}

				afterVisitValue(top);
			}
		}
	}

	@Override
	protected void afterVisitProperty(String name) {
		final DeserializationElement top = elements.peek();

		if (top.isProcessedProperty()) {
			if (!isList(top.getInstance())) {
				try {
					Field field = top.getTypeToken().getRawType().getDeclaredField(name);
					TypeToken<?> typeToken = TypeToken.of(field.getGenericType());
					DeserializationElement child = new DeserializationElement(typeToken, name);
					elements.push(child);
				} catch (NoSuchFieldException e) {
					elements.push(DeserializationElement.skippedProperty(currentPath()));
				} catch (SecurityException e) {
					throw new DeserializationException(
							"Could not access field \"" + name + "\" of type " + top.getTypeToken().getRawType(), e);
				}
			} else {
				throw new MalformedJsonException("Unexpected Property \"" + name + "\", " + "expected Array elements.");
			}
		} else {
			elements.push(DeserializationElement.skippedProperty(currentPath()));
		}
	}

	@Override
	protected void afterVisitValue(String value) {
		DeserializationElement top = elements.peek();

		if (top.isProcessedProperty()) {
			DeserializationElement current;

			if (isList(top.getInstance())) {
				Class<?> memberType = getListMemberTypeToken(top.getTypeToken()).getRawType();
				
				if (memberType.equals(String.class)) {
					current = new DeserializationElement(TypeToken.of(String.class),
							(Object) value);
				} else if (Enum.class.isAssignableFrom(memberType)) {
					current = new DeserializationElement(TypeToken.of(memberType),
							determineEnumValue(memberType, value));
				} else {
					TypeToken<?> memberTypeToken = getListMemberTypeToken(top.getTypeToken());
					current = new DeserializationElement(memberTypeToken,
							context.getValueForType(memberTypeToken, value));
				}
			} else {
				Class<?> fieldType = top.getTypeToken().getRawType();
				
				if (fieldType.equals(String.class)) {
					top.setInstance(value);
				} else if (Enum.class.isAssignableFrom(fieldType)) {
					top.setInstance(determineEnumValue(fieldType, value));
				} else {
					top.setInstance(context.getValueForType(top.getTypeToken(), value));
				}
				
				current = elements.pop();
			}

			afterVisitValue(current);
		} else {
			if (top.getElementType() != ElementType.ARRAY) {
				elements.pop();
			}
		}
	}

	private Object determineEnumValue(Class<?> enumClass, String enumName) {
		final Field[] fields = enumClass.getFields();

		for (final Field field : fields) {
			try {
				if (field.getName().equals(enumName)) {
					return field.get(null);
				}
			} catch (final Exception ignored) {
				// shouldn't happen
			}
		}

		return null;
	}

	@Override
	protected void afterVisitValue(Number value) {
		DeserializationElement top = elements.peek();

		if (top.isProcessedProperty()) {
			DeserializationElement current;

			if (isList(top.getInstance())) {
				TypeToken<?> memberTypeToken = getListMemberTypeToken(top.getTypeToken());
				current = new DeserializationElement(memberTypeToken,
						context.getValueForType(memberTypeToken, value));
			} else {
				top.setInstance(context.getValueForType(top.getTypeToken(), value));
				current = elements.pop();
			}

			afterVisitValue(current);
		} else {
			if (top.getElementType() != ElementType.ARRAY) {
				elements.pop();
			}
		}
	}

	@Override
	protected void afterVisitValue(Boolean value) {
		DeserializationElement top = elements.peek();
		
		if (top.isProcessedProperty()) {
			DeserializationElement current;

			if (isList(top.getInstance())) {
				current = new DeserializationElement(TypeToken.of(Boolean.class), value);
			} else {
				top.setInstance(value);
				current = elements.pop();
			}

			afterVisitValue(current);
		} else {
			if (top.getElementType() != ElementType.ARRAY) {
				elements.pop();
			}
		}
	}

	@Override
	protected void afterVisitNullValue() {
		DeserializationElement top = elements.peek();
		
		if (top.isProcessedProperty()) {
			DeserializationElement current;

			if (isList(top.getInstance())) {
				current = new DeserializationElement(TypeToken.of(Object.class), (Object) null);
			} else {
				top.setInstance(null);
				current = elements.pop();
			}

			afterVisitValue(current);
		} else {
			if (top.getElementType() != ElementType.ARRAY) {
				elements.pop();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void afterVisitValue(DeserializationElement value) {
		DeserializationElement top = elements.peek();

		if (isList(top.getInstance())) {
			((List) top.getInstance()).add(value.getInstance());
		} else {
			String parentField = value.getParentField();
			try {
				Field field = top.getTypeToken().getRawType().getDeclaredField(parentField);
				boolean accessible = field.isAccessible();

				try {
					field.setAccessible(true);
					field.set(top.getInstance(), value.getInstance());
				} finally {
					field.setAccessible(accessible);
				}
			} catch (NoSuchFieldException e) {
				// ignored to allow skipping properties of no interest
			} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
				throw new DeserializationException("Setting value " + value.getInstance() + " on field \"" + parentField
						+ "\" of instance " + top.getInstance() + " failed. Cause is: ", e);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected R afterGetVisitingResult(R result) {
		if (elements.size() == 1) {
			return (R) elements.pop().getInstance();
		}

		throw new IllegalStateException("Unexpected method call," +
				" deserialization has not finished yet.");
	}
}

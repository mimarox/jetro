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
package net.sf.jetro.object.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class TypeToken<T> {
	private final Class<T> rawType;
	private final Type type;

	/**
	 * Constructs a new type literal. Derives represented class from type
	 * parameter.
	 *
	 * <p>Clients create an empty anonymous subclass. Doing so embeds the type
	 * parameter in the anonymous class's type hierarchy so we can reconstitute it
	 * at runtime despite erasure.
	 */
	@SuppressWarnings("unchecked")
	protected TypeToken() {
		this.type = getSuperclassTypeParameter(getClass());
		this.rawType = (Class<T>) getRawType(this.type);
	}

	@SuppressWarnings("unchecked")
	private TypeToken(Type type) {
		this.type = type;
		this.rawType = (Class<T>) getRawType(type);
	}

	/**
	 * Returns the type from super class's type parameter.
	 */
	private static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return parameterized.getActualTypeArguments()[0];
	}

	private static Type getRawType(Type type) {
		Type rawType = null;

		if (type instanceof Class) {
			rawType = type;
		} else if (type instanceof ParameterizedType) {
			rawType = ((ParameterizedType) type).getRawType();
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			rawType = getArrayClass(getRawType(genericArrayType.getGenericComponentType()));
		} else if (type instanceof TypeVariable) {
			// First bound is always the "primary" bound that determines the runtime signature.
			rawType = getRawType(((TypeVariable<?>) type).getBounds()[0]);
		} else if (type instanceof WildcardType) {
			// Wildcard can have one and only one upper bound.
			rawType = getRawType(((WildcardType) type).getUpperBounds()[0]);
		} else {
			throw new AssertionError(type + " unsupported");
		}

		return rawType;
	}

	private static Type getArrayClass(Type componentType) {
		return Array.newInstance((Class<?>) componentType, 0).getClass();
	}

	public Type getType() {
		return type;
	}

	public Class<T> getRawType() {
		return rawType;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isAssignableFrom(Class<?> clazz) {
		boolean assignable = false;

		if (type == clazz || type.equals(clazz)) {
			assignable = true;
		} else if (type instanceof Class) {
			assignable = ((Class) type).isAssignableFrom(clazz);
		} else {
			assignable = rawType.isAssignableFrom(clazz);
		}

		return assignable;
	}

	public static TypeToken<?> of(Type type) {
		return new TypeToken<Object>(type);
	}

	public static <T> TypeToken<T> of(Class<T> clazz) {
		return new TypeToken<T>(clazz);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TypeToken [type=").append(type).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		TypeToken other = (TypeToken) obj;
		return Objects.equals(type, other.type);
	}
}

package net.sf.jetro.object.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class TypeToken<T> {
	private final Class<T> rawType;
	private final Type type;
	private final int hashCode;

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
		this.rawType = (Class<T>) getRawType(type);
		this.hashCode = type.hashCode();
	}

	private TypeToken(Type type) {
		this.type = type;
		this.rawType = (Class<T>) getRawType(type);
		this.hashCode = type.hashCode();
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

	@Override
	public int hashCode() {
		return hashCode;
	}

	public static TypeToken<?> of(Type type) {
		return new TypeToken<Object>(type);
	}
}

package net.sf.jetro.util;

import java.lang.reflect.Field;

/**
 * Utilities related to {@link Class} objects.
 * 
 * @author Matthias Rothe
 */
public final class ClassUtils {
	private ClassUtils() {}
	
	/**
	 * Find a field of the given name in the given class or any of its parent classes.
	 * 
	 * @param clazz the class object to start the search at
	 * @param name the name of the field to find
	 * @return the field of the class or any of its parent classes with the given name
	 * @throws NoSuchFieldException if and only if no field could be found
	 */
	public static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
		if (clazz == null) {
			throw new NoSuchFieldException(name);
		}
		
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return findField(clazz.getSuperclass(), name);
		}
	}
}

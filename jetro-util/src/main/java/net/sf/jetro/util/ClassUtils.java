package net.sf.jetro.util;

import java.lang.reflect.Field;

public final class ClassUtils {
	private ClassUtils() {}
	
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

package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.reflect.TypeToken;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public interface InstanceCreator<T> {

	T createInstance(TypeToken<T> typeToken);
}

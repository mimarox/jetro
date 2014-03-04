package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public interface TypeSerializer<T> {

	boolean canSerialize(Object toSerialize);

	void serialize(T toSerialize, JsonVisitor<?> recipient);
}

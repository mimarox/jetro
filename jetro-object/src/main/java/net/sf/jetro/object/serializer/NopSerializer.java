package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class NopSerializer implements TypeSerializer<Object> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return false; // so this will never be accidentally selected
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		// do nothing
	}
}

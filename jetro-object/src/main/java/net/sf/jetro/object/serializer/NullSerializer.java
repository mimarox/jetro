package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class NullSerializer implements TypeSerializer<Object> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize == null;
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		recipient.visitNullValue();
	}
}

package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class NumberSerializer implements TypeSerializer<Number> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Number;
	}

	@Override
	public void serialize(Number toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize);
	}
}

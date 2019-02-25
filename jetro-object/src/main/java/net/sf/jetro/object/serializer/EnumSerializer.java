package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

public class EnumSerializer<T extends Enum<T>> implements TypeSerializer<T> {

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Enum;
	}

	@Override
	public void serialize(T toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize.name());
	}
}

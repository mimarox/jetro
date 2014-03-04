package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class BooleanSerializer implements TypeSerializer<Boolean> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Boolean;
	}

	@Override
	public void serialize(Boolean toSerialize, JsonVisitor<?> recipient) {
		if (toSerialize == null) {
			recipient.visitNullValue();
		} else {
			recipient.visitValue(toSerialize);
		}
	}
}

package net.sf.jetro.object.serializer.addons;

import java.util.Date;

import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.visitor.JsonVisitor;

public class DateSerializer implements TypeSerializer<Date> {

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Date;
	}

	@Override
	public void serialize(Date toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize.getTime());
	}
}

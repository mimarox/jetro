package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class ArraySerializer implements TypeSerializer<Object> {
	private SerializationContext context;

	public ArraySerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize != null && toSerialize.getClass().isArray();
	}

	@Override
	public void serialize(Object toSerialize, JsonVisitor<?> recipient) {
		JsonArrayVisitor<?> arrayVisitor = recipient.visitArray();

		for (int i = 0, length = Array.getLength(toSerialize); i < length; i++) {
			Object element = Array.get(toSerialize, i);
			TypeSerializer<Object> serializer = context.getSerializer(element);
			serializer.serialize(element, arrayVisitor);
		}

		arrayVisitor.visitEnd();
	}
}

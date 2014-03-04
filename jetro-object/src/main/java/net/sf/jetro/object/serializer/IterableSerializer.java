package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class IterableSerializer implements TypeSerializer<Iterable<?>> {
	private SerializationContext context;

	public IterableSerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Iterable;
	}

	@Override
	public void serialize(Iterable<?> toSerialize, JsonVisitor<?> recipient) {
		if (toSerialize == null) {
			recipient.visitNullValue();
		} else {
			JsonArrayVisitor<?> arrayVisitor = recipient.visitArray();

			for (Object element : toSerialize) {
				TypeSerializer<Object> serializer = context.getSerializer(element);
				serializer.serialize(element, arrayVisitor);
			}

			arrayVisitor.visitEnd();
		}
	}
}
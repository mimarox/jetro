package net.sf.jetro.object.visitor;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.serializer.TypeSerializer;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectVisitingReader implements VisitingReader {
	private SerializationContext context;
	private Object toSerialize;

	public ObjectVisitingReader(SerializationContext context, Object toSerialize) {
		this.context = context;
		this.toSerialize = toSerialize;
	}

	@Override
	public void accept(JsonVisitor<?> visitor) {
		TypeSerializer<Object> serializer = context.getSerializer(toSerialize);
		serializer.serialize(toSerialize, visitor);
	}
}

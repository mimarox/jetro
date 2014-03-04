package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class MapSerializer implements TypeSerializer<Map<?, ?>> {
	private SerializationContext context;

	public MapSerializer(SerializationContext context) {
		this.context = context;
	}

	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Map;
	}

	@Override
	public void serialize(Map<?, ?> toSerialize, JsonVisitor<?> recipient) {
		if (toSerialize == null) {
			recipient.visitNullValue();
			return;
		}

		JsonObjectVisitor<?> objectVisitor = recipient.visitObject();

		for (Entry<?, ?> entry : toSerialize.entrySet()) {
			TypeSerializer<Object> serializer = context.getSerializer(entry.getValue());

			objectVisitor.visitProperty(entry.getKey().toString());
			serializer.serialize(entry.getValue(), objectVisitor);
		}

		objectVisitor.visitEnd();
	}
}

package net.sf.jetro.object;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class ObjectMerger {
	private SerializationContext context;
	private Object toMerge;

	public ObjectMerger(Object toMerge) {
		this(new SerializationContext(), toMerge);
	}

	public ObjectMerger(SerializationContext context, Object toMerge) {
		this.context = context;
		this.toMerge = toMerge;
	}

	public void into(JsonVisitor<?> visitor) {
		ObjectVisitingReader reader = new ObjectVisitingReader(context, toMerge);
		reader.accept(visitor);
	}
}

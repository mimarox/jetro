package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.visitor.JsonVisitor;

/**
 * Created by matthias.rothe on 07.07.14.
 */
public interface TypeDeserializer<T> {
	boolean canDeserialize(TypeToken<T> typeToken);
	JsonVisitor<T> getVisitorFor(TypeToken<T> typeToken);
}

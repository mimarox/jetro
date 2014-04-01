package net.sf.jetro.object.deserializer;

import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.visitor.JsonVisitor;

import java.lang.reflect.Type;

/**
 * @author matthias.rothe
 * @since 26.03.14.
 */
public class DeserializationContext {
	private ObjectConstructor objectConstructor = new ObjectConstructor();

	public <T> void addInstanceCreator(TypeToken<T> typeToken, InstanceCreator<T> instanceCreator) {
		objectConstructor.addInstanceCreator(typeToken, instanceCreator);
	}

	public <T> T createInstanceOf(Type type) {
		return (T) objectConstructor.constructFrom(type);
	}

	public <R> JsonVisitor<R> getArrayVisitorFor(TypeToken<R> typeToken) {
		return null;
	}

	public <R> JsonVisitor<R> getObjectVisitorFor(TypeToken<R> typeToken) {
		return null;
	}
}

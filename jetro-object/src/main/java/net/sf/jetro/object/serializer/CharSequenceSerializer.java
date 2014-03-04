package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * @author matthias.rothe
 * @since 26.02.14.
 */
public class CharSequenceSerializer implements TypeSerializer<CharSequence> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof CharSequence;
	}

	@Override
	public void serialize(CharSequence toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize.toString());
	}
}
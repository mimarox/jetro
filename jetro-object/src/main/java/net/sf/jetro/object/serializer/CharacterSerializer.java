package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.JsonVisitor;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class CharacterSerializer implements TypeSerializer<Character> {
	@Override
	public boolean canSerialize(Object toSerialize) {
		return toSerialize instanceof Character;
	}

	@Override
	public void serialize(Character toSerialize, JsonVisitor<?> recipient) {
		recipient.visitValue(toSerialize.toString());
	}
}

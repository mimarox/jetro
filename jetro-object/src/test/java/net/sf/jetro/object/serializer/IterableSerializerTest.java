package net.sf.jetro.object.serializer;

import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;
import org.testng.annotations.Test;

/**
 * Created by matthias.rothe on 26.02.14.
 */
public class IterableSerializerTest {

	@Test
	public void testSerialization() {
		TypeSerializer<Object> serializer = getSerializer();
		serializer.serialize(new String[]{ "foo", "bar" }, new UniformChainedJsonVisitor<Object>() {});
	}

	private TypeSerializer<Object> getSerializer() {
		return (TypeSerializer) new IterableSerializer(new SerializationContext());
	}
}

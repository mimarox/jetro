package net.sf.jetro.object.annotations;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;

public class JsonIgnorePropertiesTest {

	@Test
	public void shouldIgnoreProperties() {
		SampleBeanA sampleBean = new SampleBeanA("foo", "bar", 1L, 2L);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(sampleBean,
				new SerializationContext());
		
		JsonReturningVisitor visitor = new JsonReturningVisitor();
		
		reader.accept(visitor);
		
		String actual = visitor.getVisitingResult();
		
		String expected = "{\"longA\":1,\"stringA\":\"foo\"}";
		
		assertEquals(actual, expected);
	}
}

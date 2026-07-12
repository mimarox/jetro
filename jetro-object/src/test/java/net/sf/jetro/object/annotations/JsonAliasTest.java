package net.sf.jetro.object.annotations;

import static org.testng.Assert.assertEquals;

import java.io.StringReader;

import org.testng.annotations.Test;

import net.sf.jetro.object.ObjectMapper;
import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.StreamVisitingReader;

public class JsonAliasTest {

	@Test
	public void shouldAliasPropertyNameWhenSerializing() {
		SampleBeanC sampleBean = new SampleBeanC("foo");
		
		ObjectVisitingReader reader = new ObjectVisitingReader(sampleBean,
				new SerializationContext());
		
		JsonReturningVisitor visitor = new JsonReturningVisitor();
		
		reader.accept(visitor);
		
		String actual = visitor.getVisitingResult();
		
		String expected = "{\"stringB\":\"foo\"}";
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldAliasPropertyNameWhenDeserializing() {
		String json = "{\"stringB\":\"foo\"}";
		
		ObjectMapper mapper = new ObjectMapper();
		SampleBeanC actual = mapper.fromJson(json, SampleBeanC.class);
		
		SampleBeanC expected = new SampleBeanC("foo");
		
		assertEquals(actual, expected);
	}
}

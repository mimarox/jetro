package net.sf.jetro.object.annotations;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;

public class JsonIgnoreTest {

	@Test
	public void shouldIgnoreProperties() {
		SampleBeanB sampleBean = new SampleBeanB("foo", "bar", 1L, 2L);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(sampleBean,
				new SerializationContext());
		
		JsonReturningVisitor visitor = new JsonReturningVisitor();
		
		reader.accept(visitor);
		
		String actual = visitor.getVisitingResult();
		
		String expected = "{\"longA\":1,\"stringA\":\"foo\"}";
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldIgnorePropertiesOnSuperclass() {
		SampleBeanE sampleBean = new SampleBeanE("foo", "bar", 1L, 2L, "bac", "bad", 3L, 4L);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(sampleBean,
				new SerializationContext());
		
		JsonReturningVisitor visitor = new JsonReturningVisitor();
		
		reader.accept(visitor);
		
		String actual = visitor.getVisitingResult();
		
		String expected = "{\"longA\":1,\"longC\":3,\"stringA\":\"foo\",\"stringC\":\"bac\"}";
		
		assertEquals(actual, expected);
	}
	
	@Test
	public void shouldIgnoreWithJsonIgnoreOnBeanAndJsonIgnorePropertiesOnSuperclass() {
		SampleBeanF sampleBean = new SampleBeanF("foo", "bar", 1L, 2L, "bac", "bad", 3L, 4L);
		
		ObjectVisitingReader reader = new ObjectVisitingReader(sampleBean,
				new SerializationContext());
		
		JsonReturningVisitor visitor = new JsonReturningVisitor();
		
		reader.accept(visitor);
		
		String actual = visitor.getVisitingResult();
		
		String expected = "{\"longA\":1,\"longC\":3,\"stringA\":\"foo\",\"stringC\":\"bac\"}";
		
		assertEquals(actual, expected);
	}
}

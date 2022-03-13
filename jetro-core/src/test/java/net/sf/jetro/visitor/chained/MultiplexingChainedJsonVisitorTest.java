package net.sf.jetro.visitor.chained;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import net.sf.jetro.visitor.DummyVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;

public class MultiplexingChainedJsonVisitorTest {
	private static final String EXPECTED = "{key: [null, 2.0, <&>, true, "
			+ "[>&<, bar, ], {key: äöü, }, ], foo: bar, }, ";
	
	@Test
	public void testStringReturningJsonVisitor() {
		JsonVisitor<String> visitor = new StringReturningJsonVisitor();
		VisitingReader reader = new DummyVisitingReader();
		
		reader.accept(visitor);
		String actual = visitor.getVisitingResult();
		
		System.out.println(actual);
	}
	
	@Test(dependsOnMethods = "testStringReturningJsonVisitor")
	public void testSimpleMultiplexingJsonVisitor() {
		JsonVisitor<String> masterVisitor = new StringReturningJsonVisitor();
		JsonVisitor<String> slaveVisitor = new StringReturningJsonVisitor();
		JsonVisitor<String> multiplexer =
				new MultiplexingJsonVisitor<String>(masterVisitor, slaveVisitor);
		
		JsonVisitor<String> visitor = new UniformChainedJsonVisitorImpl<String>(multiplexer);
		
		VisitingReader reader = new DummyVisitingReader();
		
		reader.accept(visitor);
		String masterActual = visitor.getVisitingResult();
		String slaveActual = slaveVisitor.getVisitingResult();
		
		assertEquals(masterActual, EXPECTED);
		assertEquals(slaveActual, EXPECTED);
	}
}

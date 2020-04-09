package net.sf.jetro.transform;

import org.testng.annotations.Test;

import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

public class TransformSourceCollectorTest {
	private static final VisitingReader SOURCE = new VisitingReader() {
		
		@Override
		public void accept(JsonVisitor<?> visitor) {
			throw new UnsupportedOperationException();
		}
	};
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullSourceOnConstructor() {
		new TransformSourceCollector(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "specification must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullTransformationSpecification() {
		new TransformSourceCollector(SOURCE).applying((TransformationSpecification) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "transformer must not be null",
			groups = "negativeTests")
	public <R> void shouldThrowExceptionNullChainedJsonVisitor() {
		new TransformSourceCollector(SOURCE).applying((ChainedJsonVisitor<R>) null);
	}
}

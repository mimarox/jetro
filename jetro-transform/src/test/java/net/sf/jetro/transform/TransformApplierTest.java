package net.sf.jetro.transform;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.testng.annotations.Test;

import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

public class TransformApplierTest {
	private static final VisitingReader SOURCE = new VisitingReader() {
		
		@Override
		public void accept(JsonVisitor<?> visitor) {
			throw new UnsupportedOperationException();
		}
	};

	private static final ChainedJsonVisitor<?> TRANSFORMER =
			new ChainedJsonVisitor<Void>() {};
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullSourceTransformerOnConstructor() {
		new TransformApplier<>(null, TRANSFORMER);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "transformer must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionSourceNullTransformerOnConstructor() {
		new TransformApplier<>(SOURCE, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullRenderContext() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsJson(null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "clazz must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullClass() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject((Class<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "clazz must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullClassDeserializationContext() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject((Class<?>) null,
				DeserializationContext.getDefault());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionClassNullDeserializationContext() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject(String.class, null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "typeToken must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullTypeToken() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject(
				(TypeToken<?>) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "typeToken must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullTypeTokenDeserializationContext() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject(
				(TypeToken<?>) null, DeserializationContext.getDefault());
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionTypeTokenNullDeserializationContext() {
		new TransformApplier<>(SOURCE, TRANSFORMER).andReturnAsObject(
				TypeToken.of(String.class), null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "target must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullOutputStream() {
		new TransformApplier<>(SOURCE, TRANSFORMER).writingTo((OutputStream) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "target must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullOutputStreamCharsetName()
			throws UnsupportedEncodingException {
		new TransformApplier<>(SOURCE, TRANSFORMER).writingTo(
				(OutputStream) null, "UTF-8");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "charsetName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionOutputStreamNullCharsetName()
			throws UnsupportedEncodingException {
		new TransformApplier<>(SOURCE, TRANSFORMER).writingTo(
				new ByteArrayOutputStream(), null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "target must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullWriter() {
		new TransformApplier<>(SOURCE, TRANSFORMER).writingTo((Writer) null);
	}
}

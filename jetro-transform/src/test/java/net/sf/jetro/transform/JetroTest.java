package net.sf.jetro.transform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.tree.JsonType;

public class JetroTest {

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullStringSource() {
		Jetro.transform((String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullInputStreamSource() {
		Jetro.transform((InputStream) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullInputStreamSourceCharsetName()
			throws UnsupportedEncodingException {
		Jetro.transform((InputStream) null, "UTF-8");
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "charsetName must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionInputStreamSourceNullCharsetName()
			throws UnsupportedEncodingException {
		Jetro.transform(new ByteArrayInputStream(new byte[] {}), (String) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullReaderSource() {
		Jetro.transform((Reader) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullJsonTypeSource() {
		Jetro.transform((JsonType) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullObjectSource() {
		Jetro.transform((Object) null);
	}
	
	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "source must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionNullObjectSourceSerializationContext() {
		Jetro.transform((Object) null, new SerializationContext());
	}

	@Test(expectedExceptions = NullPointerException.class,
			expectedExceptionsMessageRegExp = "context must not be null",
			groups = "negativeTests")
	public void shouldThrowExceptionObjectSourceNullSerializationContext() {
		Jetro.transform(new Object(), null);
	}
}

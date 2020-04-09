package net.sf.jetro.transform;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.VisitingReader;

public class Jetro {
	private static final String SOURCE_NOT_NULL = "source must not be null";
	private Jetro() {}
	
	public static TransformSourceCollector transform(final InputStream source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		
		try {
			return transform(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static TransformSourceCollector transform(final InputStream source,
			final String charsetName) throws UnsupportedEncodingException {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		return transform(new InputStreamReader(source, charsetName));
	}
	
	public static TransformSourceCollector transform(final Reader source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(getStreamVisitingReader(source));
	}

	public static TransformSourceCollector transform(final String source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(getStreamVisitingReader(
				new StringReader(source)));
	}

	private static VisitingReader getStreamVisitingReader(final Reader source) {
		return new StreamVisitingReader(new JsonReader(source));
	}
	
	public static TransformSourceCollector transform(final JsonType source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(new JsonElementVisitingReader(source));
	}

	public static TransformSourceCollector transform(final Object source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return transform(source, new SerializationContext());
	}
	
	public static TransformSourceCollector transform(final Object source,
			final SerializationContext context) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		Objects.requireNonNull(context, "context must not be null");
		return new TransformSourceCollector(new ObjectVisitingReader(source, context));
	}
}

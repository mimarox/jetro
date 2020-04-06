package net.sf.jetro.transform;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import net.sf.jetro.object.serializer.SerializationContext;
import net.sf.jetro.object.visitor.ObjectVisitingReader;
import net.sf.jetro.stream.JsonReader;
import net.sf.jetro.stream.visitor.StreamVisitingReader;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.VisitingReader;

public class Jetro {

	private Jetro() {}
	
	public static TransformSourceCollector transform(final InputStream source) {
		try {
			return transform(new InputStreamReader(source, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static TransformSourceCollector transform(final InputStream source,
			final String charsetName) throws UnsupportedEncodingException {
		return transform(new InputStreamReader(source, charsetName));
	}
	
	public static TransformSourceCollector transform(final Reader source) {
		return new TransformSourceCollector(getStreamVisitingReader(source));
	}

	public static TransformSourceCollector transform(final String source) {
		return new TransformSourceCollector(getStreamVisitingReader(
				new StringReader(source)));
	}

	private static VisitingReader getStreamVisitingReader(final Reader source) {
		return new StreamVisitingReader(new JsonReader(source));
	}
	
	public static TransformSourceCollector transform(final JsonType source) {
		return new TransformSourceCollector(new JsonElementVisitingReader(source));
	}

	public static TransformSourceCollector transform(final Object source) {
		return new TransformSourceCollector(new ObjectVisitingReader(source,
				new SerializationContext()));
	}
	
	public static TransformSourceCollector transform(final Object source,
			final SerializationContext context) {
		return new TransformSourceCollector(new ObjectVisitingReader(source, context));
	}
}

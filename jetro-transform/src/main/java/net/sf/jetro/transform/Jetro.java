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
import net.sf.jetro.transform.highlevel.TransformationSpecification;
import net.sf.jetro.tree.JsonType;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

/**
 * Main entry point to the transformation API.
 * <p>
 * Each transformation follows the pattern
 * <code>Jetro.transform(source).applying(transformation).&lt;method of
 * {@link TransformApplier}&gt;()</code>.
 * <p>
 * <code>source</code> can be any representation of JSON. See the individual
 * <code>transform</code> methods for possible types.
 * <p>
 * <code>transformation</code> can either be an instance of any subtype of
 * {@link ChainedJsonVisitor} or an instance of an implementation of
 * {@link TransformationSpecification}. For details see there.
 * <p>
 * While the methods <code>transform</code> and <code>applying</code> collect
 * the data for the transformation, the actual transformation only ever happens
 * in the call to a method of the TransformApplier class.
 * 
 * @author Matthias Rothe
 */
public class Jetro {
	private static final String SOURCE_NOT_NULL = "source must not be null";
	private Jetro() {}
	
	/**
	 * Transform JSON retrieved from the given {@link InputStream}. If the
	 * InputStream yields anything but a valid JSON document, an exception
	 * will be thrown during the transformation.
	 * <p>
	 * This method expects the JSON document to be encoded with UTF-8.
	 * 
	 * @param source the InputStream to retrieve the JSON document from
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final InputStream source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		
		try {
			return transform(source, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Transform JSON retrieved from the given {@link InputStream}. If the
	 * InputStream yields anything but a valid JSON document, an exception
	 * will be thrown during the transformation.
	 * 
	 * @param source the InputStream to retrieve the JSON document from
	 * @param charsetName the name of the charset the JSON document is encoded with
	 * @return an instance of {@link TransformSourceCollector}
	 * @throws UnsupportedEncodingException if the named charset is not supported
	 */
	public static TransformSourceCollector transform(final InputStream source,
			final String charsetName) throws UnsupportedEncodingException {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		return transform(new InputStreamReader(source, charsetName));
	}
	
	/**
	 * Transform JSON retrieved from the given {@link Reader}. If the
	 * Reader yields anything but a valid JSON document, an exception
	 * will be thrown during the transformation.
	 * 
	 * @param source the Reader to retrieve the JSON document from
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final Reader source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(getStreamVisitingReader(source));
	}

	/**
	 * Transform JSON given as a {@link String}. If the given String contains
	 * anything but a valid JSON document, an exception will be thrown during
	 * the transformation.
	 * 
	 * @param source the JSON document
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final String source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(getStreamVisitingReader(
				new StringReader(source)));
	}

	private static VisitingReader getStreamVisitingReader(final Reader source) {
		return new StreamVisitingReader(new JsonReader(source));
	}
	
	/**
	 * Transform the given {@link JsonType}.
	 * 
	 * @param source the JSON document
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final JsonType source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return new TransformSourceCollector(new JsonElementVisitingReader(source));
	}

	/**
	 * Serialize the given {@link Object} to JSON and transform it.
	 * <p>
	 * The source may be of any type the default {@link SerializationContext}
	 * can serialize.
	 * 
	 * @param source the Object to serialize and transform
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final Object source) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		return transform(source, new SerializationContext());
	}
	
	/**
	 * Serialize the given {@link Object} to JSON using the given
	 * {@link SerializationContext} and transform it.
	 * <p>
	 * The source may be of any type the given {@link SerializationContext}
	 * can serialize.
	 * 
	 * @param source the Object to serialize and transform
	 * @return an instance of {@link TransformSourceCollector}
	 */
	public static TransformSourceCollector transform(final Object source,
			final SerializationContext context) {
		Objects.requireNonNull(source, SOURCE_NOT_NULL);
		Objects.requireNonNull(context, "context must not be null");
		return new TransformSourceCollector(new ObjectVisitingReader(source, context));
	}
}

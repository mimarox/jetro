package net.sf.jetro.transform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Objects;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.object.deserializer.DeserializationContext;
import net.sf.jetro.object.reflect.TypeToken;
import net.sf.jetro.object.visitor.ObjectBuildingVisitor;
import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.stream.visitor.JsonWritingVisitor;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.visitor.JsonTreeBuildingVisitor;
import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.VisitingReader;
import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

/**
 * Part of the transformation API.
 * <p>
 * The methods of this class perform the actual transformation(s) if any and
 * either output or return the resulting JSON in some way.
 * 
 * @param <R> the return type of the method
 * {@link ChainedJsonVisitor#getVisitingResult()} of the transformer given to
 * the constructor
 * 
 * @author Matthias Rothe
 * @see Jetro
 */
public class TransformApplier<R> {
	private final VisitingReader source;
	private final ChainedJsonVisitor<R> transformer;
	
	TransformApplier(final VisitingReader source, final ChainedJsonVisitor<R> transformer) {
		Objects.requireNonNull(source, "source must not be null");	
		Objects.requireNonNull(transformer, "transformer must not be null");
		
		this.source = source;
		this.transformer = transformer;
	}

	/**
	 * Perform the transformation(s) if any and write the resulting JSON to the given
	 * {@link OutputStream}.
	 * <p>
	 * The JSON is written with UTF-8 encoding.
	 * 
	 * @param target the OutputStream to write to
	 */
	public void writingTo(final OutputStream target) {
		Objects.requireNonNull(target, "target must not be null");
		
		try {
			writingTo(target, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Perform the transformation(s) if any and write the resulting JSON to the given
	 * {@link OutputStream} using the charset with the given name to encode the chars.
	 * 
	 * @param target the OutputStream to write to
	 * @param charsetName the name of the charset to use
	 * @throws UnsupportedEncodingException if the named charset is not supported
	 */
	public void writingTo(final OutputStream target, final String charsetName)
			throws UnsupportedEncodingException {
		Objects.requireNonNull(target, "target must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		writingTo(new OutputStreamWriter(target, charsetName));
	}
	
	/**
	 * Perform the transformation(s) if any and write the resulting JSON to the given
	 * {@link Writer}.
	 * 
	 * @param target the Writer to write to
	 */
	@SuppressWarnings("unchecked")
	public void writingTo(final Writer target) {
		Objects.requireNonNull(target, "target must not be null");
		
		try (JsonWriter writer = new JsonWriter(target)) {
			JsonWritingVisitor visitor = new JsonWritingVisitor(writer);
			transformer.attachVisitor((JsonVisitor<R>) visitor);	
			source.accept(transformer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as an
	 * {@link InputStream}.
	 * <p>
	 * The JSON is encoded with UTF-8. The default {@link RenderContext} is used.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson()} in a {@link ByteArrayInputStream}.
	 * 
	 * @return the InputStream containing the resulting JSON.
	 * @throws RuntimeException if the executing platform doesn't support UTF-8
	 */
	public InputStream andReturnAsInputStream() {
		try {
			return new ByteArrayInputStream(andReturnAsJson().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON using
	 * the given {@link RenderContext} as an {@link InputStream}.
	 * <p>
	 * The JSON is encoded with UTF-8.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson(RenderContext)} in a {@link ByteArrayInputStream}.
	 * 
	 * @param context the RenderContext to use to render the resulting JSON
	 * @return the InputStream containing the resulting JSON.
	 */
	public InputStream andReturnAsInputStream(final RenderContext context) {
		Objects.requireNonNull(context, "context must not be null");

		try {
			return new ByteArrayInputStream(andReturnAsJson(context).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as an
	 * {@link InputStream} encoding the chars with the given named charset.
	 * <p>
	 * The default {@link RenderContext} is used.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson()} in a {@link ByteArrayInputStream}.
	 * 
	 * @param charsetName the name of the charset to encode with
	 * @return the InputStream containing the resulting JSON.
	 * @throws UnsupportedEncodingException if the named charset is not supported
	 */
	public InputStream andReturnAsInputStream(final String charsetName)
			throws UnsupportedEncodingException {
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		return new ByteArrayInputStream(andReturnAsJson().getBytes(charsetName));
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON using
	 * the given {@link RenderContext} as an {@link InputStream} encoding the chars
	 * with the given named charset.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson(RenderContext)} in a {@link ByteArrayInputStream}.
	 * 
	 * @param context the RenderContext to use to render the resulting JSON
	 * @param charsetName the name of the charset to encode with
	 * @return the InputStream containing the resulting JSON.
	 * @throws UnsupportedEncodingException if the named charset is not supported
	 */
	public InputStream andReturnAsInputStream(final RenderContext context,
			final String charsetName) throws UnsupportedEncodingException {
		Objects.requireNonNull(context, "context must not be null");
		Objects.requireNonNull(charsetName, "charsetName must not be null");
		
		return new ByteArrayInputStream(andReturnAsJson(context).getBytes(charsetName));
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as a
	 * {@link Reader}.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson()} in a {@link StringReader}.
	 * 
	 * @return the Reader containing the resulting JSON
	 */
	public Reader andReturnAsReader() {
		return new StringReader(andReturnAsJson());
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON using the
	 * given {@link RenderContext} as a {@link Reader}.
	 * <p>
	 * This is a convenience method wrapping the JSON returned by
	 * {@link #andReturnAsJson(RenderContext)} in a {@link StringReader}.
	 * 
	 * @param context the RenderContext to use to render the resulting JSON
	 * @return the Reader containing the resulting JSON
	 */
	public Reader andReturnAsReader(final RenderContext context) {
		Objects.requireNonNull(context, "context must not be null");
		return new StringReader(andReturnAsJson(context));
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as a
	 * {@link JsonElement}. This JsonElement should usually be an instance of
	 * either {@link JsonObject} or {@link JsonArray}. 
	 * 
	 * @return the resulting JSON as a JsonElement
	 */
	public JsonElement andReturnAsJsonElement() {
		return applyTransformation(new JsonTreeBuildingVisitor());
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as a
	 * {@link String} using the default {@link RenderContext}.
	 * 
	 * @return the resulting JSON
	 */
	public String andReturnAsJson() {
		return applyTransformation(new JsonReturningVisitor());
	}
	
	/**
	 * Perform the transformation(s) if any and return the resulting JSON as a
	 * {@link String} using the given {@link RenderContext}.
	 * 
	 * @param context the RenderContext to use to render the resulting JSON
	 * @return the resulting JSON
	 */
	public String andReturnAsJson(final RenderContext context) {
		Objects.requireNonNull(context, "context must not be null");
		return applyTransformation(new JsonReturningVisitor(context));
	}
	
	/**
	 * Perform the transformation(s) if any, deserialize the resulting JSON and
	 * return the resulting object.
	 * <p>
	 * The default {@link DeserializationContext} is used.
	 * 
	 * @param <T> the type of the object to return
	 * @param clazz the class object of the type to return
	 * @return the resulting object
	 */
	public <T> T andReturnAsObject(final Class<T> clazz) {
		Objects.requireNonNull(clazz, "clazz must not be null");
		return andReturnAsObject(TypeToken.of(clazz));
	}
	
	/**
	 * Perform the transformation(s) if any, deserialize the resulting JSON using
	 * the given {@link DeserializationContext} and return the resulting object.
	 * 
	 * @param <T> the type of the object to return
	 * @param clazz the class object of the type to return
	 * @param context the DeserializationContext to use for deserialization
	 * @return the resulting object
	 */
	public <T> T andReturnAsObject(final Class<T> clazz,
			final DeserializationContext context) {
		Objects.requireNonNull(clazz, "clazz must not be null");
		Objects.requireNonNull(context, "context must not be null");
		
		return andReturnAsObject(TypeToken.of(clazz), context);
	}
	
	/**
	 * Perform the transformation(s) if any, deserialize the resulting JSON and
	 * return the resulting object.
	 * <p>
	 * The default {@link DeserializationContext} is used.
	 * 
	 * @param <T> the type of the object to return
	 * @param typeToken the {@link TypeToken} of the type to return
	 * @return the resulting object
	 */
	public <T> T andReturnAsObject(final TypeToken<T> typeToken) {
		Objects.requireNonNull(typeToken, "typeToken must not be null");
		return andReturnAsObject(typeToken, DeserializationContext.getDefault());
	}
	
	/**
	 * Perform the transformation(s) if any, deserialize the resulting JSON using
	 * the given {@link DeserializationContext} and return the resulting object.
	 * 
	 * @param <T> the type of the object to return
	 * @param typeToken the {@link TypeToken} of the type to return
	 * @param context the DeserializationContext to use for deserialization
	 * @return the resulting object
	 */
	public <T> T andReturnAsObject(final TypeToken<T> typeToken,
			final DeserializationContext context) {
		Objects.requireNonNull(typeToken, "typeToken must not be null");
		Objects.requireNonNull(context, "context must not be null");
		
		return applyTransformation(new ObjectBuildingVisitor<T>(typeToken, context));
	}
	
	@SuppressWarnings("unchecked")
	private <R2> R2 applyTransformation(final JsonVisitor<R2> visitor) {
		transformer.attachVisitor((JsonVisitor<R>) visitor);
		source.accept(transformer);
		return visitor.getVisitingResult();		
	}
}

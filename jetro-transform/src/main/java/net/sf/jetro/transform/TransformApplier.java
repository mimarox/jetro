package net.sf.jetro.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

public class TransformApplier<R> {
	private final VisitingReader source;
	private final ChainedJsonVisitor<R> transformer;
	
	TransformApplier(final VisitingReader source, final ChainedJsonVisitor<R> transformer) {
		Objects.requireNonNull(source, "source must not be null");	
		Objects.requireNonNull(transformer, "transformer must not be null");
		
		this.source = source;
		this.transformer = transformer;
	}

	public void writingTo(final OutputStream target) {
		try {
			writingTo(new OutputStreamWriter(target, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void writingTo(final OutputStream target, final String charsetName)
			throws UnsupportedEncodingException {
		writingTo(new OutputStreamWriter(target, charsetName));		
	}
	
	@SuppressWarnings("unchecked")
	public void writingTo(final Writer target) {
		try (JsonWriter writer = new JsonWriter(target)) {
			JsonWritingVisitor visitor = new JsonWritingVisitor(writer);
			transformer.attachVisitor((JsonVisitor<R>) visitor);
			
			source.accept(transformer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JsonElement andReturnAsJsonElement() {
		return applyTransformation(new JsonTreeBuildingVisitor());
	}
	
	public String andReturnAsJson() {
		return applyTransformation(new JsonReturningVisitor());
	}
	
	public String andReturnAsJson(final RenderContext context) {
		return applyTransformation(new JsonReturningVisitor(context));
	}
	
	public <T> T andReturnAsObject(final Class<T> clazz) {
		return andReturnAsObject(TypeToken.of(clazz));
	}
	
	public <T> T andReturnAsObject(final Class<T> clazz,
			final DeserializationContext context) {
		return andReturnAsObject(TypeToken.of(clazz), context);
	}
	
	public <T> T andReturnAsObject(final TypeToken<T> typeToken) {
		return andReturnAsObject(typeToken, DeserializationContext.getDefault());
	}
	
	public <T> T andReturnAsObject(final TypeToken<T> typeToken,
			final DeserializationContext context) {
		return applyTransformation(new ObjectBuildingVisitor<T>(typeToken, context));
	}
	
	@SuppressWarnings("unchecked")
	private <R2> R2 applyTransformation(final JsonVisitor<R2> visitor) {
		transformer.attachVisitor((JsonVisitor<R>) visitor);
		source.accept(transformer);
		return visitor.getVisitingResult();		
	}
}

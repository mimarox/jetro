package net.sf.jetro.stream.visitor;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.stream.JsonGenerator;
import net.sf.jetro.util.FastAppendable;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;

import java.nio.CharBuffer;

public final class JsonReturningVisitor extends AbstractJsonWritingVisitor<String> {
	private class JsonReturningObjectVisitor extends AbstractJsonWritingObjectVisitor<String> {
		@Override
		protected JsonObjectVisitor<String> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<String> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonGenerator getGenerator() {
			return JsonReturningVisitor.this.getGenerator();
		}

		@Override
		public String getVisitingResult() {
			return JsonReturningVisitor.this.getVisitingResult();
		}
	}

	private class JsonReturningArrayVisitor extends AbstractJsonWritingArrayVisitor<String> {
		@Override
		protected JsonObjectVisitor<String> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<String> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonGenerator getGenerator() {
			return JsonReturningVisitor.this.getGenerator();
		}

		@Override
		public String getVisitingResult() {
			return JsonReturningVisitor.this.getVisitingResult();
		}
	}

	private StringBuilder buffer;
	private JsonGenerator generator;
	private RenderContext context;

	private JsonObjectVisitor<String> objectVisitor = new JsonReturningObjectVisitor();
	private JsonArrayVisitor<String> arrayVisitor = new JsonReturningArrayVisitor();

	public JsonReturningVisitor() {
		this(new RenderContext());
	}

	public JsonReturningVisitor(final RenderContext context) {
		if (context == null) {
			throw new IllegalArgumentException("context must not be null");
		}

		this.context = context;
		initJsonGenerator();
	}

	@Override
	protected JsonObjectVisitor<String> newJsonObjectVisitor() {
		return objectVisitor;
	}

	@Override
	protected JsonArrayVisitor<String> newJsonArrayVisitor() {
		return arrayVisitor;
	}

	@Override
	protected JsonGenerator getGenerator() {
		return generator;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	@Override
	public String getVisitingResult() {
		String result = buffer.toString();

		buffer.delete(0, buffer.length());
		generator.reset();

		return result;
	}

	private void initJsonGenerator() {
		buffer = new StringBuilder(1024);
		generator = new JsonGenerator(buffer);
		generator.setHtmlSafe(context.isHtmlSafe());
		generator.setIndent(context.getIndent());
		generator.setLenient(context.isLenient());
		generator.setSerializeNulls(context.isSerializeNulls());
	}
}
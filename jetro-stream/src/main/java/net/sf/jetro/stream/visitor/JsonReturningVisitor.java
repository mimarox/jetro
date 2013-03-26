package net.sf.jetro.stream.visitor;

import java.io.StringWriter;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;

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
		protected JsonWriter getWriter() {
			return JsonReturningVisitor.this.getWriter();
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
		protected JsonWriter getWriter() {
			return JsonReturningVisitor.this.getWriter();
		}

		@Override
		public String getVisitingResult() {
			return JsonReturningVisitor.this.getVisitingResult();
		}
	}

	private StringWriter buffer;
	private JsonWriter writer;
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
	protected JsonWriter getWriter() {
		initJsonWriterIfNecessary();
		return writer;
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		writer = null;
	}

	@Override
	public String getVisitingResult() {
		return buffer.toString();
	}

	private void initJsonWriterIfNecessary() {
		if (writer == null) {
			buffer = new StringWriter();
			writer = new JsonWriter(buffer);
			writer.setHtmlSafe(context.isHtmlSafe());
			writer.setIndent(context.getIndent());
			writer.setLenient(context.isLenient());
			writer.setSerializeNulls(context.isSerializeNulls());
		}
	}
}
package net.sf.jetro.stream.visitor;

import net.sf.jetro.stream.JsonWriter;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;

public final class JsonWritingVisitor extends AbstractJsonWritingVisitor<Void> {
	private class JsonWritingObjectVisitor extends AbstractJsonWritingObjectVisitor<Void> {
		@Override
		protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonWriter getWriter() {
			return writer;
		}

		@Override
		public Void getVisitingResult() {
			return null;
		}
	}

	private class JsonWritingArrayVisitor extends AbstractJsonWritingArrayVisitor<Void> {
		@Override
		protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
			return objectVisitor;
		}

		@Override
		protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
			return arrayVisitor;
		}

		@Override
		protected JsonWriter getWriter() {
			return writer;
		}

		@Override
		public Void getVisitingResult() {
			return null;
		}
	}

	private JsonWriter writer;
	private JsonObjectVisitor<Void> objectVisitor = new JsonWritingObjectVisitor();
	private JsonArrayVisitor<Void> arrayVisitor = new JsonWritingArrayVisitor();

	public JsonWritingVisitor(final JsonWriter writer) {
		if (writer == null) {
			throw new IllegalArgumentException("jsonWriter must not be null");
		}

		this.writer = writer;
	}

	@Override
	protected JsonObjectVisitor<Void> newJsonObjectVisitor() {
		return objectVisitor;
	}

	@Override
	protected JsonArrayVisitor<Void> newJsonArrayVisitor() {
		return arrayVisitor;
	}

	@Override
	protected JsonWriter getWriter() {
		return writer;
	}

	@Override
	public Void getVisitingResult() {
		return null;
	}
}
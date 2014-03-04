package net.sf.jetro.stream.visitor;

import java.io.IOException;

import net.sf.jetro.stream.JsonGenerator;
import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class AbstractJsonWritingVisitor<R> implements JsonVisitor<R> {
	@Override
	public JsonObjectVisitor<R> visitObject() {
		try {
			getGenerator().beginObject();
			return newJsonObjectVisitor();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonObjectVisitor<R> newJsonObjectVisitor();

	@Override
	public JsonArrayVisitor<R> visitArray() {
		try {
			getGenerator().beginArray();
			return newJsonArrayVisitor();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonArrayVisitor<R> newJsonArrayVisitor();

	@Override
	public void visitProperty(String name) {
		try {
			getGenerator().name(name);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(boolean value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(Number value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitValue(String value) {
		try {
			getGenerator().value(value);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitNullValue() {
		try {
			getGenerator().nullValue();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitEnd() {
		try {
			getGenerator().flush();
			getGenerator().close();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	protected abstract JsonGenerator getGenerator();
}
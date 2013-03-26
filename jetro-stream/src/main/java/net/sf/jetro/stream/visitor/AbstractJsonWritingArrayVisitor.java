package net.sf.jetro.stream.visitor;

import java.io.IOException;

import net.sf.jetro.visitor.JsonArrayVisitor;

public abstract class AbstractJsonWritingArrayVisitor<R> extends AbstractJsonWritingVisitor<R> implements
		JsonArrayVisitor<R> {
	@Override
	public void visitProperty(String name) {
		throw new UnsupportedOperationException("Cannot add a property to an array");
	}

	@Override
	public void visitEnd() {
		try {
			getWriter().endArray();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}
}
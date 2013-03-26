package net.sf.jetro.stream.visitor;

import java.io.IOException;

import net.sf.jetro.visitor.JsonObjectVisitor;

public abstract class AbstractJsonWritingObjectVisitor<R> extends AbstractJsonWritingVisitor<R> implements
		JsonObjectVisitor<R> {
	@Override
	public void visitProperty(String name) {
		try {
			getWriter().name(name);
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}

	@Override
	public void visitEnd() {
		try {
			getWriter().endObject();
		} catch (IOException e) {
			throw new JsonIOException(e);
		}
	}
}
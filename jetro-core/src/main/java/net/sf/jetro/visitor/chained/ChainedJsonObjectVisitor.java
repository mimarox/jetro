package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class ChainedJsonObjectVisitor<R> extends ChainedJsonVisitor<R> implements JsonObjectVisitor<R> {
	public ChainedJsonObjectVisitor(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}
}
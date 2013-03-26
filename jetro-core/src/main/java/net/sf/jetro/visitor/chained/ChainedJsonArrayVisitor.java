package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public class ChainedJsonArrayVisitor<R> extends ChainedJsonVisitor<R> implements JsonArrayVisitor<R> {
	public ChainedJsonArrayVisitor(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}
}
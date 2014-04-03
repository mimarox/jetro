package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonObjectVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class ChainedJsonObjectVisitor<R> extends ChainedJsonVisitor<R> implements JsonObjectVisitor<R> {
	public static final ChainedJsonObjectVisitor NO_OP_VISITOR = new ChainedJsonObjectVisitor(){};

	/**
	 * This constructor is used if the resulting object is supposed to be the end point of a
	 * visitor chain. Without overwriting any hook methods this results in a no-op visitor.
	 */
	public ChainedJsonObjectVisitor() {
	}

	public ChainedJsonObjectVisitor(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}
}
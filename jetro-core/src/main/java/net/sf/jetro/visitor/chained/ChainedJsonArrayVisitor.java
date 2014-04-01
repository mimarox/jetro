package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonArrayVisitor;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class ChainedJsonArrayVisitor<R> extends ChainedJsonVisitor<R> implements JsonArrayVisitor<R> {
	public static final ChainedJsonArrayVisitor NO_OP_VISITOR = new ChainedJsonArrayVisitor(){};

	/**
	 * This constructor is used if the resulting object is supposed to be the end point of a
	 * visitor chain. Without overwriting any hook methods this results in a no-op visitor.
	 */
	public ChainedJsonArrayVisitor() {
	}

	public ChainedJsonArrayVisitor(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}
}
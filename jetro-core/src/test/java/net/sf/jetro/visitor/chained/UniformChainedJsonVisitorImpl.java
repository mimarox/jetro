package net.sf.jetro.visitor.chained;

import net.sf.jetro.visitor.JsonVisitor;

public class UniformChainedJsonVisitorImpl<R> extends UniformChainedJsonVisitor<R> {

	public UniformChainedJsonVisitorImpl() {
	}
	
	public UniformChainedJsonVisitorImpl(JsonVisitor<R> nextVisitor) {
		super(nextVisitor);
	}
}

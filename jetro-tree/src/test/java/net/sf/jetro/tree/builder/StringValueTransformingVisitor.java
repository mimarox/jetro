package net.sf.jetro.tree.builder;

import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

public class StringValueTransformingVisitor extends UniformChainedJsonVisitor<String> {
	private final String postfix;
	
	public StringValueTransformingVisitor(final String postfix) {
		this.postfix = postfix;
	}
	
	@Override
	protected String beforeVisitValue(final String value) {
		return value + postfix;
	}
}
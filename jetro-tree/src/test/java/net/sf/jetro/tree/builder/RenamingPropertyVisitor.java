package net.sf.jetro.tree.builder;

import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

public class RenamingPropertyVisitor extends UniformChainedJsonVisitor<Void> {

	@Override
	protected String beforeVisitProperty(final String name) {
		return "renamed";
	}
}

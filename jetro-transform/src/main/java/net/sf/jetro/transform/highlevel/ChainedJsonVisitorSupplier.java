package net.sf.jetro.transform.highlevel;

import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

@FunctionalInterface
public interface ChainedJsonVisitorSupplier {
	ChainedJsonVisitor<Void> toChainedJsonVisitor();
}

package net.sf.jetro.transform.highlevel;

import net.sf.jetro.visitor.chained.ChainedJsonVisitor;

/**
 * This interface defines a method {@link #toChainedJsonVisitor()}
 * to return a {@link ChainedJsonVisitor}.
 * 
 * @author Matthias Rothe
 */
@FunctionalInterface
public interface ChainedJsonVisitorSupplier {
	
	/**
	 * Provide a {@link ChainedJsonVisitor}.
	 * 
	 * @return the ChainedJsonVisitor provided
	 */
	ChainedJsonVisitor<Void> toChainedJsonVisitor();
}

package net.sf.jetro.object.visitor;

import net.sf.jetro.visitor.JsonVisitor;
import net.sf.jetro.visitor.chained.UniformChainedJsonVisitor;

/**
 * @author matthias.rothe
 * @since 27.02.14.
 */
public class ObjectBuildingVisitor extends UniformChainedJsonVisitor<Object> {
	private Class<?> targetClass;

	public <T> ObjectBuildingVisitor(Class<T> targetClass) {

	}
}

package net.sf.jetro.visitor;

public interface JsonArrayVisitor<R> extends JsonVisitor<R> {

	/**
	 * Must always throw {@link UnsupportedOperationException} as arrays don't support properties
	 */
	void visitProperty(String name);
}
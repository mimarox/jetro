package net.sf.jetro.visitor;

public interface JsonVisitor<R> {
	JsonObjectVisitor<R> visitObject();

	JsonArrayVisitor<R> visitArray();

	void visitProperty(String name);

	void visitValue(boolean value);

	void visitValue(Number value);

	void visitValue(String value);

	void visitNullValue();

	void visitEnd();

	R getVisitingResult();
}
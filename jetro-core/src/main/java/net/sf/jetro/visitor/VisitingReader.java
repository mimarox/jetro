package net.sf.jetro.visitor;

/**
 * Generic interface for a class reading a representation of JSON and calling
 * the methods of the {@link JsonVisitor} given to the {@link #accept(JsonVisitor)}
 * method accordingly.
 * 
 * @author matthias.rothe
 */
public interface VisitingReader {

	/**
	 * Accepts a visitor to call its visit methods according to the structure of the read
	 * JSON representation.
	 * 
	 * @param visitor The accepted visitor
	 */
	void accept(JsonVisitor<?> visitor);
}
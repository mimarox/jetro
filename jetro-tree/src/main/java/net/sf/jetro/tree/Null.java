package net.sf.jetro.tree;

/**
 * This class serves as <code>null</code> value.
 * 
 * @author matthias.rothe
 */
public class Null {
	/**
	 * The only instance of this class that will ever get generated
	 */
	public static final Null instance = new Null();

	private Null() {
	}

	@Override
	public String toString() {
		return "Null";
	}
}
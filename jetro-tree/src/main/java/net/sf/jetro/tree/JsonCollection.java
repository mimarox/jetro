package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public interface JsonCollection extends JsonType {
	void recalculateTreePaths(boolean treeRoot);
	
	/**
	 * This method recalculates the tree paths
	 * taking the element it is called on as the root
	 * of the tree.
	 */
	default void recalculateTreePaths() {
		recalculateTreePaths(true);
	}
	
	void resetPathsRecursively();
	
	/**
	 * Resets the paths recursively
	 */
	default void resetPaths() {
		resetPathsRecursively();
	}

	/**
	 * Adds the given {@link JsonType element} to the JSON tree at the
	 * given {@link JsonPath path}.
	 *  
	 * @param path
	 * @param element
	 * @return <code>true</code> if and only if the element has been successfully
	 * added to the JSON tree at the given path, <code>false</code> otherwise
	 * (especially if the JSON tree already had an element at the given path)
	 */
	boolean addElementAt(JsonPath path, JsonType element);
	
	/**
	 * Replaces the element for the given {@link JsonPath path} only if it is
	 * currently set to some value.
	 *  
	 * @param path The path at which to replace the value
	 * @param element The new element
	 * @return the previous value set at the specified path
	 * or null if there was no element for the path.
	 */
	JsonType replaceElementAt(JsonPath path, JsonType element);
		
	/**
	 * Removes the {@link JsonType element} at the given {@link JsonPath path}
	 * @param path The path to remove the element at
	 */
	void removeElementAt(JsonPath path);
}

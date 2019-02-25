package net.sf.jetro.tree;

import java.util.Optional;

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
	 * @param path The path to add the element at
	 * @param element The element to add
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
	 * @return an {@link Optional} containing the previous value set at the specified
	 * path or {@link Optional#empty()} if there was no element for the path.
	 */
	Optional<JsonType> replaceElementAt(JsonPath path, JsonType element);
		
	/**
	 * Removes the {@link JsonType element} at the given {@link JsonPath path}
	 * 
	 * @param path The path to remove the element at
	 * @return <code>true</code> if and only if an element has been removed at the
	 * given path, <code>false</code> otherwise (especially if there was no element
	 * at the given path that could have been removed)
	 * @throws IndexOutOfBoundsException if the element should have been removed
	 * from a {@link JsonArray} and the index was index < 0 || index >= size().
	 */
	boolean removeElementAt(JsonPath path);
	
	/**
	 * Checks whether the JSON tree has an element at the given {@link JsonPath path}.
	 *  
	 * @param path The path to check at
	 * @return <code>true</code> if and only if the JSON tree has an element at the
	 * given path, <code>false</code> otherwise.
	 */
	default boolean hasElementAt(final JsonPath path) {
		return getElementAt(path).isPresent();
	}
}

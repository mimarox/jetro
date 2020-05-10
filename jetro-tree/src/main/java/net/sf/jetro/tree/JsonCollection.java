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
	 * Resets the paths recursively.
	 */
	@Override
	default void resetPaths() {
		resetPathsRecursively();
	}

	/**
	 * Adds an {@link JsonString element} with the given value to the JSON tree
	 * at the given {@link JsonPath path}.
	 *  
	 * @param path The path to add the element at
	 * @param value The value of the element to add
	 * @return <code>true</code> if and only if the element has been successfully
	 * added to the JSON tree at the given path, <code>false</code> otherwise
	 * (especially if the JSON tree already had an element at the given path)
	 */
	default boolean addElementAt(JsonPath path, String value) {
		return addElementAt(path, new JsonString(value));
	}
	
	/**
	 * Adds an {@link JsonNumber element} with the given value to the JSON tree
	 * at the given {@link JsonPath path}.
	 *  
	 * @param path The path to add the element at
	 * @param value The value of the element to add
	 * @return <code>true</code> if and only if the element has been successfully
	 * added to the JSON tree at the given path, <code>false</code> otherwise
	 * (especially if the JSON tree already had an element at the given path)
	 */
	default boolean addElementAt(JsonPath path, Number value) {
		return addElementAt(path, new JsonNumber(value));
	}
	
	/**
	 * Adds an {@link JsonBoolean element} with the given value to the JSON tree
	 * at the given {@link JsonPath path}.
	 * 
	 * @param path The path to add the element at
	 * @param value The value of the element to add
	 * @return <code>true</code> if and only if the element has been successfully
	 * added to the JSON tree at the given path, <code>false</code> otherwise
	 * (especially if the JSON tree already had an element at the given path)
	 */
	default boolean addElementAt(JsonPath path, Boolean value) {
		return addElementAt(path, new JsonBoolean(value));
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
	 * @param newValue The value of the new element
	 * @return an {@link Optional} containing the previous value set at the specified
	 * path or {@link Optional#empty()} if there was no element for the path.
	 */
	default Optional<JsonType> replaceElementAt(JsonPath path, String newValue) {
		return replaceElementAt(path, new JsonString(newValue));
	}
	
	/**
	 * Replaces the element for the given {@link JsonPath path} only if it is
	 * currently set to some value.
	 *  
	 * @param path The path at which to replace the value
	 * @param newValue The value of the new element
	 * @return an {@link Optional} containing the previous value set at the specified
	 * path or {@link Optional#empty()} if there was no element for the path.
	 */
	default Optional<JsonType> replaceElementAt(JsonPath path, Number newValue) {
		return replaceElementAt(path, new JsonNumber(newValue));
	}
	
	/**
	 * Replaces the element for the given {@link JsonPath path} only if it is
	 * currently set to some value.
	 *  
	 * @param path The path at which to replace the value
	 * @param newValue The value of the new element
	 * @return an {@link Optional} containing the previous value set at the specified
	 * path or {@link Optional#empty()} if there was no element for the path.
	 */
	default Optional<JsonType> replaceElementAt(JsonPath path, Boolean newValue) {
		return replaceElementAt(path, new JsonBoolean(newValue));
	}
	
	/**
	 * Replaces the element for the given {@link JsonPath path} only if it is
	 * currently set to some value.
	 *  
	 * @param path The path at which to replace the value
	 * @param newElement The new element
	 * @return an {@link Optional} containing the previous value set at the specified
	 * path or {@link Optional#empty()} if there was no element for the path.
	 */
	Optional<JsonType> replaceElementAt(JsonPath path, JsonType newElement);
		
	/**
	 * Removes the {@link JsonType element} at the given {@link JsonPath path}.
	 * 
	 * @param path The path to remove the element at
	 * @return <code>true</code> if and only if an element has been removed at the
	 * given path, <code>false</code> otherwise (especially if there was no element
	 * at the given path that could have been removed)
	 * @throws IndexOutOfBoundsException if the element should have been removed
	 * from a {@link JsonArray} and the index was index &lt; 0 || index &gt;= size().
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

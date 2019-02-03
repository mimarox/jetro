package net.sf.jetro.tree;

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
}

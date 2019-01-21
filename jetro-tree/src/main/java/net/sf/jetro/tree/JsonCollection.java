package net.sf.jetro.tree;

public interface JsonCollection extends JsonType {
	void recalculateTreePaths(boolean treeRoot);
	
	default void recalculateTreePaths() {
		recalculateTreePaths(true);
	}
}

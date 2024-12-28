package net.sf.jetro.transform.highlevel.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import net.sf.jetro.tree.JsonArray;
import net.sf.jetro.tree.JsonObject;
import net.sf.jetro.tree.JsonProperty;
import net.sf.jetro.tree.JsonType;

/**
 * This class provides access to the hierarchifying editor via the {@link #to(String)}
 * method.
 * 
 * @author Matthias Rothe
 */
public class HierarchifyingEditor {
	private final String foreignKey;
	
	private static class Node {
		private boolean resolved = false;
		private JsonObject value;
		private List<Node> children = new ArrayList<>();
		
		public static Node forUnresolvedValue(final JsonObject value) {
			Objects.requireNonNull(value, "value must not be null");
			
			Node node = new Node();
			node.value = value;
			return node;
		}
		
		public static Node forResolvedValue(final JsonObject value) {
			Node node = forUnresolvedValue(value);
			node.resolved = true;
			return node;
		}
		
		public void resolve() {
			resolved = true;
		}
		
		public boolean isResolved() {
			return resolved;
		}
		
		public void addChild(final Node child) {
			Objects.requireNonNull(child, "child must not be null");
			children.add(child);
		}
		
		public JsonObject build(final String foreignKey) {
			if (!resolved) {
				throw new EditorException("JSON object [" + value.toJson() +
						"] could not be resolved");
			}
			
			value.removeAllByKeys(Arrays.asList(foreignKey, "children"));
			
			if (!children.isEmpty()) {
				JsonArray array = new JsonArray();
				value.add(new JsonProperty("children", array));
				
				for (Node child : children) {
					array.add(child.build(foreignKey));
				}
			}
			
			return value;
		}
	}
	
	/**
	 * The package private constructor taking the given foreignKey.
	 * @param foreignKey the foreign key
	 */
	HierarchifyingEditor(final String foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	/**
	 * Provides the hierarchifying editor function. The function will hierarchify the
	 * objects in a source {@link JsonArray} according to the given <code>foreignKey</code>
	 * provided via the {@link Editors#hierarchifyMapping(String)} method and the given
	 * <code>primaryKey</code> supplied to this method.
	 * <p>
	 * The JsonArray given to the function MUST be a flat array of {@link JsonObject}s.
	 * The objects contained in the array MUST all have a property named equal to the
	 * given <code>primaryKey</code>. Some objects contained in the array MAY have a
	 * property named equal to the given <code>foreignKey</code>. The types of both such
	 * properties MUST be equal and be some JSON primitive type. The value of any
	 * <code>foreignKey</code> property MUST be equal to the value of one <code>primaryKey</code>
	 * property of a different object in the array.
	 * <p>
	 * If any of these conditions are not met the returned function MAY throw an
	 * {@link EditorException}.
	 * 
	 * @param primaryKey the primary key
	 * @return the hierarchifying editor function
	 */
	public Function<JsonArray, JsonArray> to(final String primaryKey) {
		Objects.requireNonNull(primaryKey, "primaryKey must not be null");
		
		return source -> {
			try {
				List<String> primaryKeyList = Arrays.asList(primaryKey);
				List<String> foreignKeyList = Arrays.asList(foreignKey);
				List<Node> nodes = new ArrayList<>();
				
				for (JsonType value : source) {
					JsonObject object = (JsonObject) value;
					
					if (!object.containsAllKeys(primaryKeyList)) {
						throw new EditorException("Primary key [" + primaryKey + "]"
								+ " missing in object " + object.toJson());
					}
					
					if (object.containsAllKeys(foreignKeyList)) {
						Node parent = findParent(nodes, object, primaryKey, foreignKey);
						
						if (parent != null) {
							parent.addChild(Node.forResolvedValue(object));
						} else {
							nodes.add(Node.forUnresolvedValue(object));
						}
					} else {
						nodes.add(Node.forResolvedValue(object));
					}
				}
				
				resolveAnyUnresolvedNodes(nodes, primaryKey, foreignKey);
				return build(nodes);
			} catch (EditorException e) {
				throw e;
			} catch (Exception e) {
				throw new EditorException("A general exception has occured while editing.", e);
			}
		};
	}

	private Node findParent(final List<Node> nodes, final JsonObject object, final String primaryKey,
			final String foreignKey) {
		Node parent = null;
		
		for (Node node : nodes) {
			if (node.value.get(primaryKey).equals(object.get(foreignKey))) {
				parent = node;
				break;
			} else if (!node.children.isEmpty()) {
				parent = findParent(node.children, object, primaryKey, foreignKey);
				
				if (parent != null) {
					break;
				}
			}
		}
		
		return parent;
	}

	private void resolveAnyUnresolvedNodes(final List<Node> nodes, final String primaryKey,
			final String foreignKey) {
		
		Iterator<Node> iterator = nodes.iterator();
		
		while (iterator.hasNext()) {
			Node node = iterator.next();
			
			if (!node.isResolved()) {
				Node parent = findParent(nodes, node.value, primaryKey, foreignKey);
				
				if (parent != null) {
					node.resolve();
					parent.addChild(node);
					iterator.remove();
				} else {
					throw new EditorException("JSON object [" + node.value.toJson() +
							"] could not be resolved");
				}
			}
		}
	}

	private JsonArray build(final List<Node> nodes) {
		JsonArray result = new JsonArray();
		
		for (Node node : nodes) {
			result.add(node.build(foreignKey));
		}
		
		return !result.isEmpty() ? result : null;
	}
}

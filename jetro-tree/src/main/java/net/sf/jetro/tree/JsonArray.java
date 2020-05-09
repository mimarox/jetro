/*
 * #%L
 * Jetro Tree
 * %%
 * Copyright (C) 2013 - 2016 The original author or authors.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.sf.jetro.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import net.sf.jetro.path.ArrayIndexPathElement;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.JsonObject.JsonProperties;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

public final class JsonArray extends ArrayList<JsonType> implements JsonCollection {
	private static final long serialVersionUID = -853759861392315220L;

	final Set<JsonPath> paths = new HashSet<>();

	public JsonArray() {
		paths.add(new JsonPath());
	}

	public JsonArray(final JsonPath path) {
		this(path, null);
	}

	public JsonArray(final List<? extends JsonType> values) {
		this((JsonPath) null, values);
	}

	public JsonArray(final JsonPath path, final List<? extends JsonType> values) {
		this(values, false);
		
		if (path != null) {
			paths.add(path);
		} else {
			paths.add(new JsonPath());
		}
	}

	private JsonArray(final Set<JsonPath> paths, final List<? extends JsonType> values) {
		this(values, true);
		this.paths.addAll(paths);
	}

	private JsonArray(final List<? extends JsonType> values, final boolean deepCopy) {
		if (values != null) {
			if (deepCopy) {
				for (JsonType value : values) {
					this.add(value.deepCopy());
				}
			} else {
				this.addAll(values);
			}
		}
	}
	
	@Override
	public JsonArray deepCopy() {
		return new JsonArray(paths, this);
	}

	@Override
	public void addPath(final JsonPath path) {
		paths.add(path);
	}

	@Override
	public void resetPathsRecursively() {
		paths.clear();
		forEach(element -> element.resetPaths());
	}
	
	@Override
	public void recalculateTreePaths(final boolean treeRoot) {
		if (treeRoot) {
			resetPaths();
			addPath(new JsonPath());
		}
		
		for (int i = 0; i < size(); i++) {
			JsonType element = get(i);
			
			for (JsonPath path : paths) {
				element.addPath(path.append(new ArrayIndexPathElement(i)));
			}
			
			if (element instanceof JsonCollection) {
				((JsonCollection) element).recalculateTreePaths(false);
			}
		}
	}
	
	@Override
	public String toJson() {
		return new DefaultJsonRenderer().render(this);
	}

	@Override
	public String toJson(final JsonRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public void mergeInto(JsonVisitor<?> visitor) {
		JsonElementVisitingReader reader = new JsonElementVisitingReader(this);
		reader.accept(visitor);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonArray [values=").append(super.toString())
			.append(", paths=").append(paths).append("]");
		return builder.toString();
	}

	@Override
	public Optional<JsonType> getElementAt(final JsonPath path) {
		if (paths.contains(path)) {
			return Optional.of(this);
		} else {
			Optional<JsonPath> parentPath = findParentPath(path);
			
			if (parentPath.isPresent()) {
				int expectedIndex = path.getArrayIndexAt(parentPath.get().getDepth());
	
				if (expectedIndex < size()) {
					return get(expectedIndex).getElementAt(path);
				}
			}
		}
		
		return Optional.empty();
	}
	
	private Optional<JsonPath> findParentPath(final JsonPath childPath) {
		return paths.parallelStream().filter(parentPath -> 
			parentPath.getDepth() < childPath.getDepth() &&
			childPath.isChildPathOf(parentPath) &&
			childPath.hasArrayIndexAt(parentPath.getDepth())
		).findFirst();
	}

	@Override
	public boolean addElementAt(final JsonPath path, final JsonType element) {
		Objects.requireNonNull(path, "A non-null path to add the element at must be specified");
		Objects.requireNonNull(element, "A non-null element to be added must be specified");
		
		if (path.isRootPath()) {
			throw new IllegalArgumentException("Cannot add JSON tree root");
		}
		
		boolean success = false;
		Optional<JsonType> parentElement = getElementAt(path.removeLastElement());
		
		if (parentElement.isPresent() && parentElement.get() instanceof JsonCollection) {
			if (parentElement.get() instanceof JsonArray &&
					(path.hasArrayIndexAt(path.getDepth() - 1) ||
							path.hasEndOfArrayAt(path.getDepth() - 1))) {
				JsonArray parent = prepareJsonArrayForChildManipulation(parentElement, path);
				
				try {
					if (path.hasEndOfArrayAt(path.getDepth() - 1)) {
						parent.add(element);
					} else {
						parent.add(path.getArrayIndexAt(path.getDepth() - 1), element);						
					}
					success = true;
				} catch (IndexOutOfBoundsException e) {
					success = false;
				}
			} else if (parentElement.get() instanceof JsonObject &&
					path.hasPropertyNameAt(path.getDepth() - 1)) {
				final JsonProperties parent = prepareJsonObjectForChildManipulation(
						parentElement, path).asMap();
				final String key = path.getPropertyNameAt(path.getDepth() - 1);
				
				if (!parent.containsKey(key)) {
					parent.put(key, element);
					success = true;
				}
			}
		}
		
		if (success) {
			recalculateTreePaths(isTreeRoot());
		}
		
		return success;
	}

	@Override
	public Optional<JsonType> replaceElementAt(final JsonPath path, final JsonType newElement) {
		Objects.requireNonNull(path,
				"A non-null path to replace the element at must be specified");
		Objects.requireNonNull(newElement, "A non-null element to be inserted must be specified");
		
		if (path.isRootPath()) {
			throw new IllegalArgumentException("Cannot replace JSON tree root");
		}
		
		JsonType replacedElement = null;
		Optional<JsonType> parentElement = getElementAt(path.removeLastElement());
		
		if (parentElement.isPresent() && parentElement.get() instanceof JsonCollection) {
			if (parentElement.get() instanceof JsonArray &&
					path.hasArrayIndexAt(path.getDepth() - 1)) {
				JsonArray parent = prepareJsonArrayForChildManipulation(parentElement, path);
				int index = path.getArrayIndexAt(path.getDepth() - 1);
				
				try {
					replacedElement = parent.remove(index);
					parent.add(index, newElement);
				} catch (IndexOutOfBoundsException e) {
					/* do nothing, Optional.empty() will be returned
					 * to indicate that no replacement took place
					 */
				}
			} else if (parentElement.get() instanceof JsonObject &&
					path.hasPropertyNameAt(path.getDepth() - 1)) {
				JsonProperties parent = prepareJsonObjectForChildManipulation(
						parentElement, path).asMap();
				String propertyName = path.getPropertyNameAt(path.getDepth() - 1);
				
				if (parent.containsKey(propertyName)) {
					replacedElement = parent.put(propertyName, newElement);
				}
			}
		}
		
		if (replacedElement != null) {
			recalculateTreePaths(isTreeRoot());
		}
		
		return Optional.ofNullable(replacedElement);
	}
	
	@Override
	public boolean removeElementAt(JsonPath path) {
		if (!isTreeRoot()) {
			throw new IllegalStateException(
					"removeElementAt can only be called on the JSON tree root.");
		}

		Objects.requireNonNull(path,
				"A non-null path to remove the element at must be specified");
		
		if (path.isRootPath()) {
			throw new IllegalArgumentException("Cannot remove JSON tree root");
		}
		
		boolean success = false;
		Optional<JsonType> parentElement = getElementAt(path.removeLastElement());
		
		if (parentElement.isPresent() && parentElement.get() instanceof JsonCollection) {
			if (parentElement.get() instanceof JsonArray &&
					path.hasArrayIndexAt(path.getDepth() - 1)) {
				JsonArray parent = prepareJsonArrayForChildManipulation(parentElement, path);
				
				try {
					JsonType child = parent.remove(path.getArrayIndexAt(path.getDepth() - 1));
					success = child != null;
				} catch (IndexOutOfBoundsException e) {
					success = false;
				}
			} else if (parentElement.get() instanceof JsonObject &&
					path.hasPropertyNameAt(path.getDepth() - 1)) {
				JsonObject parent = prepareJsonObjectForChildManipulation(parentElement, path);
				
				success = parent.removeAllByKeys(
						Arrays.asList(path.getPropertyNameAt(path.getDepth() - 1)));
			}
		}
		
		if (success) {
			recalculateTreePaths();
		}
		
		return success;
	}

	private boolean isTreeRoot() {
		return paths.size() == 1 && paths.contains(new JsonPath());
	}

	private JsonArray prepareJsonArrayForChildManipulation(Optional<JsonType> parentElement,
			JsonPath path) {
		JsonArray jsonArray = (JsonArray) parentElement.get();
		
		if (hasMultiplePaths(jsonArray)) {
			JsonArray shallowCopy = new JsonArray(jsonArray);
			JsonPath parentPath = path.removeLastElement();
			
			removeElementAt(parentPath);
			addElementAt(parentPath, shallowCopy);
			
			return shallowCopy;
		} else {
			return jsonArray;
		}
	}
	
	private boolean hasMultiplePaths(JsonArray jsonArray) {
		return jsonArray.paths.size() > 1;
	}
	
	private JsonObject prepareJsonObjectForChildManipulation(Optional<JsonType> parentElement,
			JsonPath path) {
		JsonObject jsonObject = (JsonObject) parentElement.get();
		
		if (hasMultiplePaths(jsonObject)) {
			JsonObject shallowCopy = new JsonObject(jsonObject);
			JsonPath parentPath = path.removeLastElement();
			
			removeElementAt(parentPath);
			addElementAt(parentPath, shallowCopy);
			
			return shallowCopy;
		} else {
			return jsonObject;
		}
	}

	private boolean hasMultiplePaths(JsonObject jsonObject) {
		return jsonObject.paths.size() > 1;
	}
}

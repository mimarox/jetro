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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.sf.jetro.path.ArrayIndexPathElement;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

public final class JsonArray extends ArrayList<JsonType> implements JsonCollection {
	private static final long serialVersionUID = -853759861392315220L;

	// JSON paths relative to the root element of the JSON tree this element belongs to
	// if empty this element is the root element
	private final Set<JsonPath> paths = new HashSet<>();

	public JsonArray() {
	}

	public JsonArray(final JsonPath path) {
		this(path, null);
	}

	public JsonArray(final List<? extends JsonType> values) {
		this((JsonPath) null, values);
	}

	public JsonArray(final JsonPath path, final List<? extends JsonType> values) {
		this(values, false);
		paths.add(path);
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
	public boolean addElementAt(JsonPath path, JsonType element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonType replaceElementAt(JsonPath path, JsonType element) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean removeElementAt(JsonPath path) {
		Optional<JsonType> parentElement = getElementAt(path.removeLastElement());
		
		if (parentElement.isPresent() && !(parentElement.get() instanceof JsonPrimitive)) {
			if (parentElement.get() instanceof JsonArray &&
					path.hasArrayIndexAt(path.getDepth() - 1)) {
				JsonType childElement = ((JsonArray) parentElement.get())
						.remove(path.getArrayIndexAt(path.getDepth() - 1));
				return childElement != null;
			}
		}
		
		return false;
	}
}

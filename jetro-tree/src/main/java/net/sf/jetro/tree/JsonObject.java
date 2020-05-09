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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.path.PropertyNamePathElement;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

public final class JsonObject extends AbstractSet<JsonProperty> implements JsonCollection {
	private static final long serialVersionUID = -2961271887393587301L;

	private final class JsonPropertiesIterator implements Iterator<Entry<String, JsonType>> {
		private Iterator<JsonProperty> backingIterator;

		private JsonPropertiesIterator(Iterator<JsonProperty> backingIterator) {
			this.backingIterator = backingIterator;
		}

		@Override
		public boolean hasNext() {
			return backingIterator.hasNext();
		}

		@Override
		public Entry<String, JsonType> next() {
			return backingIterator.next();
		}

		@Override
		public void remove() {
			backingIterator.remove();
		}
	}

	private final class JsonPropertiesSet extends AbstractSet<Entry<String, JsonType>> {
		@Override
		public Iterator<Entry<String, JsonType>> iterator() {
			return new JsonPropertiesIterator(properties.iterator());
		}

		@Override
		public int size() {
			return properties.size();
		}
	}

	public final class JsonProperties extends AbstractMap<String, JsonType> {
		private JsonPropertiesSet entrySet;

		public JsonType put(String key, String value) {
			return put(key, new JsonString(value));
		}

		public JsonType put(String key, Number value) {
			return put(key, new JsonNumber(value));
		}

		public JsonType put(String key, Boolean value) {
			return put(key, new JsonBoolean(value));
		}

		public JsonType putNullValue(String key) {
			return put(key, new JsonNull());
		}

		@Override
		public JsonType put(String key, JsonType value) {
			if (key == null) {
				throw new IllegalArgumentException("key must not be null");
			}

			JsonProperty property = null;

			Iterator<JsonProperty> iterator = properties.iterator();
			while (iterator.hasNext()) {
				JsonProperty candidate = iterator.next();
				
				if (candidate.getKey().equals(key)) {
					property = candidate.shallowCopy();
					iterator.remove();
					break;
				}
			}

			if (property == null) {
				property = new JsonProperty(key);
			}

			JsonType oldValue = property.getValue();
			property.setValue(value);
			
			properties.add(property);
			
			return oldValue;
		}

		@Override
		public Set<Entry<String, JsonType>> entrySet() {
			if (entrySet == null) {
				entrySet = new JsonPropertiesSet();
			}

			return entrySet;
		}

		public JsonObject asJsonObject() {
			return JsonObject.this;
		}
	}

	private final Set<JsonProperty> properties = new HashSet<>();
	private JsonProperties mapView;

	final Set<JsonPath> paths = new HashSet<>();

	public JsonObject() {
		paths.add(new JsonPath());
	}

	public JsonObject(final JsonPath path) {
		this(path, null);
	}

	public JsonObject(final Set<JsonProperty> properties) {
		this((JsonPath) null, properties);
	}

	public JsonObject(final JsonPath path, final Set<JsonProperty> properties) {
		this(properties, false);
		
		if (path != null) {
			paths.add(path);
		} else {
			paths.add(new JsonPath());
		}
	}

	private JsonObject(final Set<JsonPath> paths, final Set<JsonProperty> properties) {
		this(properties, true);
		this.paths.addAll(paths);
	}
	
	private JsonObject(final Set<JsonProperty> properties, final boolean deepCopy) {
		if (properties != null) {
			if (deepCopy) {
				properties.forEach(property -> add(property.deepCopy()));
			} else {
				properties.forEach(property -> add(property.shallowCopy()));
			}
		}
	}

	@Override
	public JsonObject deepCopy() {
		return new JsonObject(paths, this);
	}
	
	@Override
	public boolean add(JsonProperty e) {
		if (!properties.parallelStream().anyMatch(property -> property.keyEquals(e))) {
			return properties.add(e);
		}
		
		return false;
	}
	
	public JsonProperties asMap() {
		if (mapView == null) {
			mapView = new JsonProperties();
		}

		return mapView;
	}

	@Override
	public void addPath(final JsonPath path) {
		paths.add(path);
	}

	@Override
	public void resetPathsRecursively() {
		paths.clear();
		forEach(property -> property.getValue().resetPaths());
	}
	
	@Override
	public void recalculateTreePaths(final boolean treeRoot) {
		if (treeRoot) {
			resetPaths();
			addPath(new JsonPath());
		}
		
		for (final JsonProperty property : properties) {
			JsonType element = property.getValue();
			paths.forEach(path -> element.addPath(
					path.append(new PropertyNamePathElement(property.getKey()))));
			
			if (element instanceof JsonCollection) {
				((JsonCollection) element).recalculateTreePaths(false);
			}
		}
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public Iterator<JsonProperty> iterator() {
		return properties.iterator();
	}

	public JsonType get(Object key) {
		return asMap().get(key);
	}
	
	@Override
	public Optional<JsonType> getElementAt(final JsonPath path) {
		if (paths.contains(path)) {
			return Optional.of(this);
		} else {
			Optional<JsonPath> parentPath = findParentPath(path);
			
			if (parentPath.isPresent()) {
				String expectedName = path.getPropertyNameAt(parentPath.get().getDepth());
				return findElement(expectedName, path);
			}
		}

		return Optional.empty();
	}

	private Optional<JsonPath> findParentPath(JsonPath childPath) {
		return paths.parallelStream().filter(parentPath ->
				parentPath.getDepth() < childPath.getDepth() &&
				childPath.isChildPathOf(parentPath) &&
				childPath.hasPropertyNameAt(parentPath.getDepth())
			).findFirst();
	}

	private Optional<JsonType> findElement(String expectedName, JsonPath path) {
		Optional<JsonType> element = null;

		for (JsonProperty property : properties) {
			if (property.getKey().equals(expectedName)) {
				element = property.getValue().getElementAt(path);
				break;
			}
		}

		if (element != null) {
			return element;
		} else {
			return Optional.empty();
		}
	}

	@Override
	public boolean addElementAt(JsonPath path, JsonType element) {
		Objects.requireNonNull(path, "A non-null path to add the element at must be specified");
		Objects.requireNonNull(element, "A non-null element to be added must be specified");
		
		if (path.isRootPath()) {
			throw new IllegalArgumentException("Cannot add JSON tree root");
		}
		
		boolean success = false;
		Optional<JsonType> parentElement = getElementAt(path.removeLastElement());
		
		if (parentElement.isPresent() && parentElement.get() instanceof JsonCollection) {
			if (parentElement.get() instanceof JsonObject &&
					path.hasPropertyNameAt(path.getDepth() - 1)) {
				final JsonProperties jsonObject = prepareJsonObjectForChildManipulation(
						parentElement, path).asMap();
				final String key = path.getPropertyNameAt(path.getDepth() - 1);
				
				if (!jsonObject.containsKey(key)) {
					jsonObject.put(key, element);
					success = true;
				}
			} else if (parentElement.get() instanceof JsonArray &&
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
			}			
		}
		
		if (success) {
			recalculateTreePaths(isTreeRoot());
		}
		
		return success;
	}

	@Override
	public Optional<JsonType> replaceElementAt(JsonPath path, JsonType newElement) {
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
		
		if (parentElement.isPresent() && !(parentElement.get() instanceof JsonPrimitive)) {
			if (parentElement.get() instanceof JsonObject &&
					path.hasPropertyNameAt(path.getDepth() - 1)) {
				JsonObject parent = prepareJsonObjectForChildManipulation(parentElement, path);
				
				success = parent.removeAllByKeys(
						Arrays.asList(path.getPropertyNameAt(path.getDepth() - 1)));
			} else if (parentElement.get() instanceof JsonArray &&
					path.hasArrayIndexAt(path.getDepth() - 1)) {
				try {
					JsonArray parent = prepareJsonArrayForChildManipulation(parentElement, path);
					
					JsonType childElement = parent.remove(
							path.getArrayIndexAt(path.getDepth() - 1));
					success = childElement != null;
				} catch (IndexOutOfBoundsException e) {
					success = false;
				}
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
		builder.append("JsonObject [properties=").append(properties)
			.append(", paths=").append(paths).append("]");
		return builder.toString();
	}

	public boolean retainAllByKey(Collection<String> keys) {
		return retainOrRemoveAllByKey(keys, shouldBeRemoved -> !shouldBeRemoved);
	}
	
	public boolean removeAllByKeys(Collection<String> keys) {
		return retainOrRemoveAllByKey(keys, shouldBeRemoved -> shouldBeRemoved);
		
	}
	
	private boolean retainOrRemoveAllByKey(Collection<String> keys, Predicate<Boolean> p) {
        Objects.requireNonNull(keys);
        boolean modified = false;
        
        Iterator<JsonProperty> it = iterator();
        while (it.hasNext()) {
        	String key = it.next().getKey();

        	if (p.test(keys.contains(key))) {
                it.remove();
                modified = true;
			}
        }
        
		return modified;
	}
	
	public boolean containsAllKeys(Collection<String> keys) {
		return asMap().keySet().containsAll(keys);
	}
}

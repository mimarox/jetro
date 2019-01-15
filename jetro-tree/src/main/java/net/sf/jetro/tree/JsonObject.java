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

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class JsonObject extends AbstractSet<JsonProperty> implements JsonType {
	private static final long serialVersionUID = -2961271887393587301L;

	private class JsonPropertiesIterator implements Iterator<Entry<String, JsonType>> {
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

	private class JsonPropertiesSet extends AbstractSet<Entry<String, JsonType>> {
		@Override
		public Iterator<Entry<String, JsonType>> iterator() {
			return new JsonPropertiesIterator(properties.iterator());
		}

		@Override
		public int size() {
			return properties.size();
		}
	}

	public class JsonProperties extends AbstractMap<String, JsonType> {
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

			for (JsonProperty candidate : properties) {
				if (candidate.getKey().equals(key)) {
					property = candidate;
					break;
				}
			}

			if (property == null) {
				property = new JsonProperty(key);
				properties.add(property);
			}

			JsonType oldValue = property.getValue();
			property.setValue(value);
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

	private ArrayList<JsonProperty> properties = new ArrayList<JsonProperty>();
	private JsonProperties mapView;

	// JSON path relative to the root element of the JSON tree this element belongs to
	// if null this element is the root element
	private JsonPath path;
	private int pathDepth;

	public JsonObject() {
	}

	public JsonObject(final JsonPath path) {
		this(path, null);
	}

	public JsonObject(final Set<JsonProperty> properties) {
		this(null, properties);
	}

	public JsonObject(final JsonPath path, final Set<JsonProperty> properties) {
		this.path = path;

		if (path != null) {
			pathDepth = path.getDepth();
		}

		if (properties != null) {
			this.properties.addAll(properties);
		}
	}

	public JsonProperties asMap() {
		if (mapView == null) {
			mapView = new JsonProperties();
		}

		return mapView;
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public Iterator<JsonProperty> iterator() {
		return properties.iterator();
	}

	@Override
	public boolean add(final JsonProperty e) {
		return properties.add(e);
	}

	public JsonType get(Object key) {
		return asMap().get(key);
	}
	
	@Override
	public JsonElement getElementAt(final JsonPath path) {
		if (this.path == path || (this.path != null && this.path.equals(path))) {
			return this;
		} else if (pathDepth < path.getDepth() && path.isChildPathOf(this.path) && path.hasPropertyNameAt(pathDepth)) {
			String expectedName = path.getPropertyNameAt(pathDepth);
			return findElement(expectedName, path);
		} else {
			throw new NoSuchElementException("No JSON Element could be found at path [" + path + "]");
		}
	}

	private JsonElement findElement(String expectedName, JsonPath path) {
		JsonElement element = null;

		for (JsonProperty property : properties) {
			if (property.getKey().equals(expectedName)) {
				element = ((JsonType) property.getValue()).getElementAt(path);
			}
		}

		if (element != null) {
			return element;
		} else {
			throw new NoSuchElementException("No JSON Element could be found at path [" + path + "]");
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
		builder.append("JsonObject [properties=").append(properties).append(", path=").append(path).append("]");
		return builder.toString();
	}
}
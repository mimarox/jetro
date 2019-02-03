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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class JsonPrimitive<T> implements JsonType {
	private static final long serialVersionUID = -200661848423590056L;

	// JSON paths relative to the root element of the JSON tree this element belongs to
	// if empty this element is the root element
	protected final Set<JsonPath> paths = new HashSet<>();
	private T value;

	public JsonPrimitive() {
	}

	public JsonPrimitive(final JsonPath path) {
		this(path, null);
	}

	public JsonPrimitive(final T value) {
		this((JsonPath) null, value);
	}

	public JsonPrimitive(final JsonPath path, final T value) {
		paths.add(path);
		this.value = value;
	}
	
	protected JsonPrimitive(final Set<JsonPath> paths, final T value) {
		this.paths.addAll(paths);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
		this.value = value;
	}
	
	@Override
	public void addPath(JsonPath path) {
		paths.add(path);
	}
	
	@Override
	public void resetPaths() {
		paths.clear();
	}
	
	@Override
	public Optional<JsonType> getElementAt(JsonPath path) {
		if (this.paths.contains(path)) {
			return Optional.of(this);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public String toJson() {
		return new DefaultJsonRenderer(new RenderContext().setLenient(true)).render(this);
	}

	@Override
	public String toJson(JsonRenderer renderer) {
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
		builder.append(getClass().getSimpleName() + " [value=").append(value)
			.append(", paths=").append(paths).append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		JsonPrimitive other = (JsonPrimitive) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}

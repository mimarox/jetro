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

import java.util.Optional;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.tree.renderer.DefaultJsonRenderer;
import net.sf.jetro.tree.renderer.JsonRenderer;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;
import net.sf.jetro.visitor.JsonVisitor;

public abstract class JsonPrimitive<T> implements JsonType {
	private static final long serialVersionUID = -200661848423590056L;

	// JSON path relative to the root element of the JSON tree this element belongs to
	// if null this element is the root element
	protected JsonPath path;
	private T value;

	public JsonPrimitive() {
	}

	public JsonPrimitive(final JsonPath path) {
		this(path, null);
	}

	public JsonPrimitive(final T value) {
		this(null, value);
	}

	public JsonPrimitive(final JsonPath path, final T value) {
		this.path = path;
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
		this.value = value;
	}
	
	@Override
	public void setPath(JsonPath path) {
		this.path = path;
	}
	
	@Override
	public Optional<JsonType> getElementAt(JsonPath path) {
		if (this.path == path || (this.path != null && this.path.equals(path))) {
			return Optional.of(this.deepCopy());
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
		builder.append(getClass().getSimpleName() + " [value=").append(value).append(", path=").append(path)
			.append("]");
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
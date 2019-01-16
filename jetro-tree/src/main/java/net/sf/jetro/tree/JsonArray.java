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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JsonArray extends ArrayList<JsonType> implements JsonType {
	private static final long serialVersionUID = -853759861392315220L;

	// JSON path relative to the root element of the JSON tree this element belongs to
	// if null this element is the root element
	private JsonPath path;
	private int pathDepth;

	public JsonArray() {
	}

	public JsonArray(final JsonPath path) {
		this(path, null);
	}

	public JsonArray(final List<? extends JsonType> values) {
		this(null, values);
	}

	public JsonArray(final JsonPath path, final List<? extends JsonType> values) {
		this.path = path;

		if (path != null) {
			pathDepth = path.getDepth();
		}

		if (values != null) {
			this.addAll(values);
		}
	}

	public void setPath(final JsonPath path) {
		this.path = path;
		// recalculate the paths of any children
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
		builder.append("JsonArray [values=").append(super.toString()).append(", path=").append(path).append("]");
		return builder.toString();
	}

	@Override
	public JsonElement getElementAt(JsonPath path) {
		if (this.path == path || (this.path != null && this.path.equals(path))) {
			return this;
		} else if (pathDepth < path.getDepth() && path.isChildPathOf(this.path) && path.hasArrayIndexAt(pathDepth)) {
			int expectedIndex = path.getArrayIndexAt(pathDepth);

			if (expectedIndex < size()) {
				return get(expectedIndex).getElementAt(path);
			} else {
				throw new NoSuchElementException("No JSON Element could be found at path [" + path + "]");
			}
		} else {
			throw new NoSuchElementException("No JSON Element could be found at path [" + path + "]");
		}
	}
}

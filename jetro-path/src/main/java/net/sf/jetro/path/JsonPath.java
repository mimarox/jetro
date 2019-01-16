/*
 * #%L
 * Jetro JsonPath
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
package net.sf.jetro.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonPath implements Cloneable, Serializable {
	private static final long serialVersionUID = -7011229423184378717L;

	static final String WILDCARD = "*";
	static final String OPTIONAL = "?";

	private static JsonPathCompiler compiler;

	private JsonPathElement[] pathElements;
	private boolean containsOptionals;
	private int size;

	public JsonPath() {
		this(new JsonPathElement[] {}, false);
	}

	JsonPath(final JsonPathElement[] pathElements, final boolean containsOptionals) {
		if (pathElements == null) {
			throw new IllegalArgumentException("pathElements must not be null");
		}

		this.pathElements = pathElements;
		this.containsOptionals = containsOptionals;
		this.size = pathElements.length;
	}

	protected JsonPath clone() {
		try {
			JsonPath clone = (JsonPath) super.clone();

			clone.pathElements = Arrays.copyOf(pathElements, size + 1);

			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public JsonPath append(final JsonPathElement newElement) {
		return clone().appendInternal(newElement);
	}

	private JsonPath appendInternal(final JsonPathElement newElement) {
		pathElements[++size - 1] = newElement;
		return this;
	}

	public JsonPath replaceLastElementWith(final JsonPathElement newElement) {
		return clone().replaceWithInternal(newElement);
	}

	private JsonPath replaceWithInternal(final JsonPathElement newElement) {
		pathElements[size - 1] = newElement;
		return this;
	}

	public JsonPath removeLastElement() {
		return clone().removeInternal();
	}

	private JsonPath removeInternal() {
		pathElements[--size] = null;
		return this;
	}

	public boolean matches(final JsonPath jsonPathPattern) {
		if (size == 0 && jsonPathPattern.size == 0) {
			return true;
		} else if (size > 0 && jsonPathPattern.size == 0) {
			return false;
		}

		JsonPath applicablePattern = jsonPathPattern.removeSkippableOptionals(this);

		if (applicablePattern.pathElements[applicablePattern.size - 1] instanceof MatchesAllFurtherPathElement) {
			if (size < applicablePattern.size - 1) {
				return false;
			}
		} else if (size != applicablePattern.size) {
			return false;
		}

		for (int i = 0; i < applicablePattern.size; i++) {
			if (applicablePattern.pathElements[i] instanceof MatchesAllFurtherPathElement) {
				return true;
			} else if (pathElements[i].getClass() != applicablePattern.pathElements[i].getClass()) {
				return false;
			} else if (applicablePattern.pathElements[i].isWildcard()) {
				continue;
			} else if (!pathElements[i].equalsIgnoreOptional(applicablePattern.pathElements[i])) {
				return false;
			}
		}

		return true;
	}

	private JsonPath removeSkippableOptionals(JsonPath jsonPath) {
		if (containsOptionals) {
			List<JsonPathElement> elements = new ArrayList<JsonPathElement>();

			for (int i = 0; i < size; i++) {
				if (!isSkippableOptional(pathElements[i], (i < jsonPath.size ? jsonPath.pathElements[i] : null))) {
					elements.add(pathElements[i]);
				}
			}

			return new JsonPath(elements.toArray(new JsonPathElement[elements.size()]), false);
		} else {
			return this;
		}
	}

	private boolean isSkippableOptional(JsonPathElement candidate, JsonPathElement comparative) {
		if (candidate.isOptional()) {
			if (comparative == null) {
				return true;
			} else {
				return candidate.getClass() != comparative.getClass();
			}
		} else {
			return false;
		}
	}

	public int getDepth() {
		return size;
	}

	public boolean isParentPathOf(final JsonPath path) {
		boolean parentPath = true;

		for (int i = 0; i < size; i++) {
			if (!(pathElements[i].isOptional() || path.pathElements[i].isOptional() ||
					pathElements[i].equals(path.pathElements[i]))) {
				parentPath = false;
				break;
			}
		}

		return parentPath;
	}

	public boolean isChildPathOf(final JsonPath path) {
		if (path == null) {
			return true; // as null is interpreted as the root path
		}

		return path.isParentPathOf(this);
	}

	public boolean hasPropertyNameAt(final int depth) {
		return pathElements[depth] instanceof PropertyNamePathElement;
	}

	public String getPropertyNameAt(final int depth) {
		if (hasPropertyNameAt(depth)) {
			return ((PropertyNamePathElement) pathElements[depth]).getName();
		} else {
			throw new IllegalStateException("The path element at depth " + depth + " in path " + this
					+ " is not a property name");
		}
	}

	public boolean hasArrayIndexAt(final int depth) {
		return pathElements[depth] instanceof ArrayIndexPathElement;
	}

	public int getArrayIndexAt(final int depth) {
		if (hasArrayIndexAt(depth)) {
			return ((ArrayIndexPathElement) pathElements[depth]).getIndex();
		} else {
			throw new IllegalStateException("The path element at depth " + depth + " in path " + this
					+ " is not an array index");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(pathElements);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JsonPath other = (JsonPath) obj;
		if (!toString().equals(other.toString())) {
			return false;
		}
		return true;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("$");

		for (int i = 0; i < size; i++) {
			builder.append(pathElements[i].toString());
		}

		return builder.toString();
	}

	public static JsonPath compile(final String jsonPath) {
		if (compiler == null) {
			compiler = new JsonPathCompiler();
		}

		return compiler.compile(jsonPath);
	}
}
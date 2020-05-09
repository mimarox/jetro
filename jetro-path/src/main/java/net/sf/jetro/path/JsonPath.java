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
import java.util.Objects;

/**
 * The JsonPath is a way to make each value of a JSON document addressable and
 * accessible. Each value of a JSON document is addressable and accessible by a
 * concrete JsonPath containing neither wildcards, optionals nor an end-of-array
 * specifier. All these might however be part of JsonPath patterns. The syntax and
 * examples of both concrete paths and path patterns are given in the JavaDoc for
 * the method {@link #compile(String)}.
 * <p>
 * JsonPaths are immutable, each seemingly mutating method first clones this object
 * and performs the mutation on the clone returning it afterwards.
 * 
 * @author Matthias Rothe
 * @see #compile(String)
 */
public class JsonPath implements Cloneable, Serializable {
	private static final long serialVersionUID = -7011229423184378717L;

	static final String WILDCARD = "*";
	static final String OPTIONAL = "?";
	static final String END_OF_ARRAY = "-";

	private static JsonPathCompiler compiler;

	private JsonPathElement[] pathElements;
	private boolean containsOptionals;
	private int size;
	private String string;

	/**
	 * Create a new root JsonPath.
	 */
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected JsonPath clone() {
		try {
			JsonPath clone = (JsonPath) super.clone();

			clone.pathElements = Arrays.copyOf(pathElements, size + 1);
			clone.string = null;
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Append a {@link JsonPathElement} to a JsonPath cloned from this one
	 * and return it.
	 * 
	 * @param newElement the element to append
	 * @return the cloned JsonPath with the new element appended
	 */
	public JsonPath append(final JsonPathElement newElement) {
		return clone().appendInternal(newElement);
	}

	private JsonPath appendInternal(final JsonPathElement newElement) {
		pathElements[++size - 1] = newElement;
		return this;
	}

	/**
	 * Replace the last element of a JsonPath cloned from this one with the
	 * given {@link JsonPathElement} and return the cloned JsonPath.
	 * 
	 * @param newElement the element to replace with
	 * @return the cloned JsonPath with the replaced last element
	 */
	public JsonPath replaceLastElementWith(final JsonPathElement newElement) {
		return clone().replaceWithInternal(newElement);
	}

	private JsonPath replaceWithInternal(final JsonPathElement newElement) {
		pathElements[size - 1] = newElement;
		recalculateOptionals();
		return this;
	}

	private void recalculateOptionals() {
		containsOptionals = false;
		
		for (int i = 0; i < size; i++) {
			if (pathElements[i].isOptional()) {
				containsOptionals = true;
				break;
			}
		}
	}

	/**
	 * Remove the last element of a clone of this JsonPath and return that clone.
	 * 
	 * @return the clone of this JsonPath after having the last element removed
	 * @throws IllegalStateException if this method is called on a root JsonPath
	 */
	public JsonPath removeLastElement() {
		if (isRootPath()) {
			throw new IllegalStateException("Cannot remove last element from root path.");
		}
		return clone().removeInternal();
	}
	
	private JsonPath removeInternal() {
		pathElements[--size] = null;
		recalculateOptionals();
		return this;
	}
	
	/**
	 * Returns whether this JsonPath is a root path.
	 * 
	 * @return <code>true</code> if and only if this JsonPath is a root path,
	 * <code>false</code> otherwise.
	 */
	public boolean isRootPath() {
		return size == 0;
	}

	/**
	 * Tells whether or not this JsonPath matches the given pattern.
	 * 
	 * @param jsonPathPattern the pattern to match
	 * @return <code>true</code> if and only if this JsonPath matches the given
	 * pattern, <code>false</code> otherwise.
	 */
	public boolean matches(final JsonPath jsonPathPattern) {
		if (size == 0 && jsonPathPattern.size == 0) {
			return true;
		} else if (size > 0 && jsonPathPattern.size == 0) {
			return false;
		}

		JsonPath applicablePattern = jsonPathPattern.removeSkippableOptionals(this);

		if (sizeMatches(applicablePattern)) {
			return elementsMatch(applicablePattern);			
		} else {
			return false;
		}
	}

	private JsonPath removeSkippableOptionals(final JsonPath jsonPath) {
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

	private boolean isSkippableOptional(final JsonPathElement candidate,
			final JsonPathElement comparative) {
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

	private boolean sizeMatches(final JsonPath applicablePattern) {
		if (applicablePattern.pathElements[applicablePattern.size - 1] instanceof MatchesAllFurtherPathElement) {
			if (size < applicablePattern.size - 1) {
				return false;
			}
		} else if (size != applicablePattern.size) {
			return false;
		}
		
		return true;
	}

	private boolean elementsMatch(final JsonPath applicablePattern) {
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

	/**
	 * Returns the depth of this JsonPath.
	 * <p>
	 * For root paths 0 is returned, elements are then counted starting with 1.
	 * The other methods in this class expect 0-based indices. Therefore for
	 * example the call to find out whether the last element of this JsonPath is
	 * a property name element is
	 * <code>jsonPath.hasPropertyNameAt(jsonPath.getDepth() - 1);</code>.
	 * 
	 * @return the depth of this JsonPath
	 */
	public int getDepth() {
		return size;
	}

	public boolean isParentPathOf(final JsonPath path) {
		if (path == null) {
			return isRootPath(); // as null is interpreted as the root path
		}
		
		if (size > path.size) {
			return false;
		}
		
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
		return pathElements[depth] instanceof ArrayIndexPathElement &&
				!((ArrayIndexPathElement) pathElements[depth]).isEndOfArray();
	}

	public int getArrayIndexAt(final int depth) {
		if (hasArrayIndexAt(depth)) {
			return ((ArrayIndexPathElement) pathElements[depth]).getIndex();
		} else {
			throw new IllegalStateException("The path element at depth " + depth + " in path " + this
					+ " is not an array index");
		}
	}

	public boolean hasWildcardAt(final int depth) {
		return pathElements[depth].isWildcard();
	}

	public boolean hasOptionalAt(final int depth) {
		return pathElements[depth].isOptional();
	}

	public boolean hasEndOfArrayAt(final int depth) {
		if (pathElements[depth] instanceof ArrayIndexPathElement) {
			return ((ArrayIndexPathElement) pathElements[depth]).isEndOfArray();			
		} else {
			return false;
		}
	}
	
	public boolean containsOptionals() {
		return containsOptionals;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(toString());
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
		if (string == null) {
			StringBuilder builder = new StringBuilder("$");

			for (int i = 0; i < size; i++) {
				builder.append(pathElements[i].toString());
			}

			string = builder.toString();
		}
		
		return string;
	}

	public static JsonPath compile(final String jsonPath) {
		if (compiler == null) {
			compiler = new JsonPathCompiler();
		}

		return compiler.compile(jsonPath);
	}
}
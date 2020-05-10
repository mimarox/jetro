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
public final class JsonPath implements Cloneable, Serializable {
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

	/**
	 * Tells whether or not this JsonPath is a parent of the given JsonPath.
	 * 
	 * @param path the potential child path
	 * @return <code>true</code> if and only if this JsonPath is a parent of
	 * the given JsonPath, <code>false</code> otherwise
	 */
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

	/**
	 * Tells whether or not this JsonPath is a child of the given JsonPath.
	 * 
	 * @param path the potential parent path
	 * @return <code>true</code> if and only if this JsonPath is a child of
	 * the given JsonPath, <code>false</code> otherwise
	 */
	public boolean isChildPathOf(final JsonPath path) {
		if (path == null) {
			return true; // as null is interpreted as the root path
		}

		return path.isParentPathOf(this);
	}

	/**
	 * Tells whether or not this JsonPath has a property name element at the given
	 * depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to check for property name
	 * @return <code>true</code> if and only if this JsonPath has a property name
	 * element at the given depth, <code>false</code> otherwise 
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @see #getDepth()
	 */
	public boolean hasPropertyNameAt(final int depth) {
		return pathElements[depth] instanceof PropertyNamePathElement;
	}

	/**
	 * Returns the property name at the given depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to return the property name
	 * @return the property name at the given depth
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @throws IllegalStateException if {{@link #hasPropertyNameAt(int)} returns
	 * <code>false</code> for the given depth
	 * @see #getDepth()
	 */
	public String getPropertyNameAt(final int depth) {
		if (hasPropertyNameAt(depth)) {
			return ((PropertyNamePathElement) pathElements[depth]).getName();
		} else {
			throw new IllegalStateException("The path element at depth " + depth +
					" in path " + this + " is not a property name");
		}
	}

	/**
	 * Tells whether or not this JsonPath has an array index element at the given
	 * depth (index) and this array index element is not an end-of-array element.
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to check for array index
	 * @return <code>true</code> if and only if this JsonPath has an array index
	 * element which is not an end-of-array element at the given depth,
	 * <code>false</code> otherwise 
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @see #getDepth()
	 */
	public boolean hasArrayIndexAt(final int depth) {
		return pathElements[depth] instanceof ArrayIndexPathElement &&
				!((ArrayIndexPathElement) pathElements[depth]).isEndOfArray();
	}

	/**
	 * Returns the array index at the given depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to return the array index
	 * @return the array index at the given depth
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @throws IllegalStateException if {@link #hasArrayIndexAt(int)} returns
	 * <code>false</code> for the given depth
	 * @see #getDepth()
	 */
	public int getArrayIndexAt(final int depth) {
		if (hasArrayIndexAt(depth)) {
			return ((ArrayIndexPathElement) pathElements[depth]).getIndex();
		} else {
			throw new IllegalStateException("The path element at depth " + depth +
					" in path " + this + " is not an array index");
		}
	}

	/**
	 * Tells whether or not this JsonPath has a wildcard at the given depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to check for wildcard
	 * @return <code>true</code> if and only if this JsonPath has a wildcard at the
	 * given depth, <code>false</code> otherwise
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @see #getDepth()
	 */
	public boolean hasWildcardAt(final int depth) {
		return pathElements[depth].isWildcard();
	}

	/**
	 * Tells whether or not this JsonPath has an optional at the given depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to check for optional
	 * @return <code>true</code> if and only if this JsonPath has an optional at the
	 * given depth, <code>false</code> otherwise
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @see #getDepth()
	 */
	public boolean hasOptionalAt(final int depth) {
		return pathElements[depth].isOptional();
	}

	/**
	 * Tells whether or not this JsonPath has an end-of-array element at the given
	 * depth (index).
	 * <p>
	 * Please note that this method uses 0-based indices.
	 * 
	 * @param depth the depth at which to check for end-of-array
	 * @return <code>true</code> if and only if this JsonPath has an end-of-array
	 * element at the given depth, <code>false</code> otherwise
	 * @throws ArrayIndexOutOfBoundsException if the depth is either negative or
	 * greater than or equal to the depth of this JsonPath
	 * @see #getDepth()
	 */	
	public boolean hasEndOfArrayAt(final int depth) {
		if (pathElements[depth] instanceof ArrayIndexPathElement) {
			return ((ArrayIndexPathElement) pathElements[depth]).isEndOfArray();			
		} else {
			return false;
		}
	}
	
	/**
	 * Tells whether or not this JsonPath contains optional elements.
	 * 
	 * @return <code>true</code> if and only if this JsonPath contains optional
	 * elements, <code>false</code> otherwise
	 */
	public boolean containsOptionals() {
		return containsOptionals;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(toString());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
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

	/**
	 * Compiles the given jsonPath String to a JsonPath object and returns it.
	 * <p>
	 * A valid JsonPath must start with a root specifier. This is the dollar sign
	 * ($). It may be followed by zero or more path elements. These path elements
	 * may either be property name, array index or a matching-all-further 
	 * element.
	 * <h2>Reserved characters</h2>
	 * These reserved characters are used in JsonPath:
	 * <ul>
	 * 	<li>$ specifies the document root
	 * 	<li>. specifies the start of a property name
	 * 	<li>[ specifies the start of an array index
	 * 	<li>] specifies the end of an array index
	 * 	<li>: specifies the matching all further element
	 * 	<li>* specifies a wildcard
	 * 	<li>? specifies an optional
	 * 	<li>- specifies the end of an array
	 * </ul>
	 * <h2>Non-reserved characters</h2>
	 * Any UTF-8 characters other than the ones given in the section
	 * &quot;Reserved characters&quot; are non-reserved characters.
	 * <h2>Property name elements</h2>
	 * Property name elements start with a dot (.) followed by a name consisting
	 * of at least one or more non-reserved characters as defined in the section
	 * &quot;Non-reserved characters&quot;. There is one exception to this rule:
	 * minus (-) characters may also be used in a property name. Property names
	 * may also consist of a single asterisk (*) which is used as a wildcard.
	 * Partial wildcards are not supported. Furthermore property name elements may
	 * be followed by a question mark (?) which specifies the property name element
	 * so annotated as optional.
	 * <h2>Array index elements</h2>
	 * Array index elements start with an opening square bracket ([) followed by
	 * either a non-negative base-10 integer without leading zeroes which is used
	 * as an array index, a single asterisk (*) which is used as a wildcard or the
	 * end-of-array specifier (-) followed by a closing square bracket (]). Please
	 * note that if used the end-of-array element must be the last element of the
	 * given JsonPath. Furthermore array index elements may be followed by a question
	 * mark (?) which specifies the array index element so annotated as optional.
	 * <h2>Matching-all-further elements</h2>
	 * Matching-all-further elements are specified by a colon (:) and must be the last
	 * element of any given JsonPath if they are used. They allow a parent path to
	 * match any child path.
	 * <h2>Examples</h2>
	 * Given the following JSON document:
	 * <pre>
	 * {
	 * 	"foo": {
	 * 		"foo-a": "value foo-a",
	 * 		"foo-b": {
	 * 			"foo-b-a": "value foo-b-a"
	 * 		},
	 * 		"foo-c": [1,2,[3]]
	 * 	},
	 * 	"bar": [{
	 * 			"bar-a": "value bar-a"
	 * 		},{
	 * 			"bar-b": "value bar-b"
	 * 	}],
	 * 	"bar-a": "value root bar-a"
	 * }
	 * </pre>
	 * Then:
	 * <pre>
	 * $                  addresses the entire document
	 * $.foo              addresses the object assigned to the property named foo
	 * $.foo.foo-a        addresses the string assigned to the property named foo-a ("value foo-a")
	 * $.foo.foo-c[0]     addresses the 0th element of the array assigned to the property named foo-c (1)
	 * $.foo.foo-c[2][0]  addresses the value 3
	 * $.foo.*            addresses all the values assigned to any of the properties of the object assigned to the property named foo
	 * $.foo.foo-c[2]?[0] addresses values 1 and 3
	 * $.bar[0].bar-a     addresses the string assigned to the property named bar-a ("value bar-a")
	 * $.bar[*]           addresses all the elements of the array assigned to the property named bar
	 * $.bar?[0]?.bar-a   addresses both values assigned to properties named bar-a ("value bar-a" and "value root bar-a")
	 * $.bar[-]           addresses the end of the array assigned to the property named bar
	 * $.bar[*]:          addresses all the elements of the array assigned to the property named bar and all their children recursively
	 * </pre>
	 * <p>
	 * This method is Thread-safe.
	 * 
	 * @param jsonPath the String to compile
	 * @return the compiled JsonPath
	 * @throws JsonPathCompilerException if the given jsonPath cannot be compiled
	 * @throws IllegalArgumentException if the given jsonPath is null or empty
	 */
	public static JsonPath compile(final String jsonPath) {
		synchronized (JsonPath.class) {
			if (compiler == null) {
				compiler = new JsonPathCompiler();
			}
		}

		return compiler.compile(jsonPath);
	}
}
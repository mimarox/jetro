package net.sf.jetro.path;

import java.io.Serializable;
import java.util.Arrays;

public class JsonPath implements Cloneable, Serializable {
	private static final long serialVersionUID = -7011229423184378717L;

	static final String WILDCARD = "*";

	private static JsonPathCompiler compiler;

	private JsonPathElement[] pathElements;
	private int size;

	public JsonPath() {
		this(new JsonPathElement[] {});
	}

	JsonPath(final JsonPathElement[] pathElements) {
		if (pathElements == null) {
			throw new IllegalArgumentException("pathElements must not be null");
		}

		this.pathElements = pathElements;
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
		if (jsonPathPattern.pathElements[jsonPathPattern.size - 1] instanceof MatchesAllFurtherPathElement) {
			if (size < jsonPathPattern.size - 1) {
				return false;
			}
		} else if (size != jsonPathPattern.size) {
			return false;
		}

		for (int i = 0; i < jsonPathPattern.size; i++) {
			if (jsonPathPattern.pathElements[i] instanceof MatchesAllFurtherPathElement) {
				return true;
			} else if (pathElements[i].getClass() != jsonPathPattern.pathElements[i].getClass()) {
				return false;
			} else if (jsonPathPattern.pathElements[i].isWildcard()) {
				continue;
			} else if (!pathElements[i].equals(jsonPathPattern.pathElements[i])) {
				return false;
			}
		}

		return true;
	}

	public int getDepth() {
		return size;
	}

	public boolean isParentPathOf(final JsonPath path) {
		boolean parentPath = true;

		for (int i = 0; i < size; i++) {
			if (pathElements[i].equals(path.pathElements[i])) {
				parentPath = false;
				break;
			}
		}

		return parentPath;
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
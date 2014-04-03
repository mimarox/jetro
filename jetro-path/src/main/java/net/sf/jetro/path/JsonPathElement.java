package net.sf.jetro.path;

import java.io.Serializable;

abstract class JsonPathElement implements Serializable {
	private static final long serialVersionUID = -3286390362790937905L;
	private boolean wildcard;
	private boolean optional;

	JsonPathElement(final boolean wildcard, final boolean optional) {
		this.wildcard = wildcard;
		this.optional = optional;
	}

	boolean isWildcard() {
		return wildcard;
	}

	boolean isOptional() {
		return optional;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JsonPathElement that = (JsonPathElement) o;
		if (optional != that.optional) return false;

		return equalsIgnoreOptional(that);
	}

	public boolean equalsIgnoreOptional(JsonPathElement other) {
		return other != null && wildcard == other.wildcard;
	}

	@Override
	public int hashCode() {
		int result = (wildcard ? 1 : 0);
		result = 31 * result + (optional ? 1 : 0);
		return result;
	}

	public abstract String toString();
}
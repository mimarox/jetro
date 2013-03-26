package net.sf.jetro.path;

import java.io.Serializable;

abstract class JsonPathElement implements Serializable {
	private static final long serialVersionUID = -3286390362790937905L;
	private boolean wildcard;

	JsonPathElement(final boolean wildcard) {
		this.wildcard = wildcard;
	}

	boolean isWildcard() {
		return wildcard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (wildcard ? 1231 : 1237);
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
		JsonPathElement other = (JsonPathElement) obj;
		if (wildcard != other.wildcard)
			return false;
		return true;
	}

	public abstract String toString();
}
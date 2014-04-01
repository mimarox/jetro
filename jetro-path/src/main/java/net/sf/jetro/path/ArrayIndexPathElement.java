package net.sf.jetro.path;

public class ArrayIndexPathElement extends JsonPathElement {
	private static final long serialVersionUID = -4814966817683544359L;
	private int index;

	public ArrayIndexPathElement(final int index) {
		this(index, false);
	}

	ArrayIndexPathElement(final int index, final boolean optional) {
		super(false, optional);

		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		this.index = index;
	}

	ArrayIndexPathElement(final boolean wildcard, final boolean optional) {
		super(wildcard, optional);
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");

		if (isWildcard()) {
			builder.append(JsonPath.WILDCARD);
		} else {
			builder.append(index);
		}

		builder.append("]");

		if (isOptional()) {
			builder.append(JsonPath.OPTIONAL);
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		return equalsIgnoreOptional((JsonPathElement) obj);
	}

	@Override
	public boolean equalsIgnoreOptional(JsonPathElement other) {
		if (this == other)
			return true;
		if (getClass() != other.getClass())
			return false;
		ArrayIndexPathElement that = (ArrayIndexPathElement) other;
		if (index != that.index)
			return false;
		return super.equalsIgnoreOptional(other);
	}
}
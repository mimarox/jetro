package net.sf.jetro.path;

public class ArrayIndexPathElement extends JsonPathElement {
	private static final long serialVersionUID = -4814966817683544359L;
	private int index;

	public ArrayIndexPathElement(int index) {
		super(false);

		if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		this.index = index;
	}

	ArrayIndexPathElement(boolean wildcard) {
		super(wildcard);
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

		return builder.append("]").toString();
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
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayIndexPathElement other = (ArrayIndexPathElement) obj;
		if (index != other.index)
			return false;
		return true;
	}
}
package net.sf.jetro.path;

public class PropertyNamePathElement extends JsonPathElement {
	private static final long serialVersionUID = 588262327967230142L;
	private String name;

	public PropertyNamePathElement(final String name) {
		super(false);

		if (name == null || "".equals(name)) {
			throw new IllegalArgumentException("name must not be null or empty");
		}

		this.name = name;
	}

	PropertyNamePathElement(final boolean wildcard) {
		super(wildcard);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(".");

		if (isWildcard()) {
			builder.append(JsonPath.WILDCARD);
		} else {
			builder.append(name);
		}

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PropertyNamePathElement other = (PropertyNamePathElement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
package net.sf.jetro.patch.pointer;

import java.io.Serializable;
import java.util.Objects;

abstract class JsonPointerElement<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -8641148961106248902L;
	private final T value;
	
	JsonPointerElement(final T value) {
		Objects.requireNonNull(value);
		
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public String toString() {
		return "/" + value.toString().replaceAll("~", "~0").replaceAll("/", "~1");
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
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
		@SuppressWarnings("rawtypes")
		JsonPointerElement other = (JsonPointerElement) obj;
		return Objects.equals(value, other.value);
	}
}

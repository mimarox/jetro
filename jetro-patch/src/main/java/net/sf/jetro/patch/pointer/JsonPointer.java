package net.sf.jetro.patch.pointer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import net.sf.jetro.path.ArrayIndexPathElement;
import net.sf.jetro.path.JsonPath;
import net.sf.jetro.path.PropertyNamePathElement;

public class JsonPointer implements Cloneable, Serializable {
	private static final long serialVersionUID = 2433043640914648968L;

	private JsonPointerElement<?>[] pointerElements; 
	private int size;
	
	public JsonPointer() {
		this(new JsonPointerElement<?>[] {});
	}
	
	JsonPointer(final JsonPointerElement<?>[] pointerElements) {
		if (pointerElements == null) {
			throw new IllegalArgumentException("pointerElements must not be null");
		}
		
		this.pointerElements = pointerElements;
		this.size = pointerElements.length;
	}

	protected JsonPointer clone() {
		try {
			JsonPointer clone = (JsonPointer) super.clone();

			clone.pointerElements = Arrays.copyOf(pointerElements, size + 1);
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public JsonPath toJsonPath() {
		JsonPath path = new JsonPath();
		
		for (int i = 0; i < size; i++) {
			if (pointerElements[i] instanceof ArrayIndexPointerElement) {
				path = path.append(new ArrayIndexPathElement(
						((ArrayIndexPointerElement) pointerElements[i]).getValue()));
			} else if (pointerElements[i] instanceof PropertyNamePointerElement) {
				path = path.append(new PropertyNamePathElement(
						((PropertyNamePointerElement) pointerElements[i]).getValue()));
			}
		}
		
		return path;
	}

	public String toString() {
		if (size == 0) {
			return "/";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < size; i++) {
			builder.append(pointerElements[i].toString());
		}
		
		return builder.toString();
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
		JsonPointer other = (JsonPointer) obj;
		return Objects.equals(toString(), other.toString());
	}

	public static JsonPointer fromJsonPath(final JsonPath path) {
		if (path == null) {
			return null;
		}
		
		if (path.isRootPath()) {
			return new JsonPointer();
		}
		
		if (path.containsOptionals()) {
			throw new IllegalArgumentException("JsonPaths with optional elements cannot be "
					+ "converted into JsonPointers");
		}
		
		final JsonPointerElement<?>[] pointerElements =
				new JsonPointerElement<?>[path.getDepth()];
		
		for (int i = 0; i < path.getDepth(); i++) {
			if (path.hasWildcardAt(i)) {
				throw new IllegalArgumentException("JsonPaths with wildcard elements cannot be "
						+ "converted into JsonPointers");
			} else if (path.hasArrayIndexAt(i)) {
				pointerElements[i] = new ArrayIndexPointerElement(path.getArrayIndexAt(i));
			} else if (path.hasPropertyNameAt(i)) {
				pointerElements[i] = PropertyNamePointerElement.of(path.getPropertyNameAt(i));
			}
		}
		
		return new JsonPointer(pointerElements);
	}

	public static JsonPointer compile(final String jsonPointer) {
		Objects.requireNonNull(jsonPointer);
		
		if (!jsonPointer.startsWith("/")) {
			throw new IllegalArgumentException("jsonPointer is not a valid JsonPointer, "
					+ "since it doesn't start with a separator (/)");
		}
		
		if (jsonPointer.equals("/")) {
			return new JsonPointer();
		}
		
		String[] parts = jsonPointer.split("/");
		
		if (parts.length < 2) {
			throw new IllegalArgumentException("jsonPointer is not a valid JsonPointer, "
					+ "since no separators (/) were found");
		}
		
		JsonPointerElement<?>[] pointerElements = new JsonPointerElement<?>[parts.length - 1];
		
		for (int i = 1; i < parts.length; i++) {
			if (isArrayIndex(parts[i])) {
				pointerElements[i - 1] = new ArrayIndexPointerElement(
						Integer.parseInt(parts[i]));
			} else {
				pointerElements[i - 1] = PropertyNamePointerElement.of(parts[i]);
			}
		}
		
		return new JsonPointer(pointerElements);
	}

	private static boolean isArrayIndex(String part) {
		return part.equals("0") || part.matches("[1-9][0-9]*");
	}
}

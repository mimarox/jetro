package net.sf.jetro.patch.pointer;

import java.util.Objects;

public class PropertyNamePointerElement extends JsonPointerElement<String> {
	private static final long serialVersionUID = -5239144044156146911L;

	private PropertyNamePointerElement(final String value) {
		super(value);
	}
	
	public static PropertyNamePointerElement of(final String value) {
		Objects.requireNonNull(value);
		String convertedValue = value.replaceAll("~1", "/").replaceAll("~0", "~");
		return new PropertyNamePointerElement(convertedValue);
	}
}

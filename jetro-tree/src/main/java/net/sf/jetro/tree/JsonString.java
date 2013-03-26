package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public class JsonString extends JsonPrimitive<String> {
	private static final long serialVersionUID = 39487332732636472L;

	public JsonString() {
	}

	public JsonString(final JsonPath path) {
		this(path, null);
	}

	public JsonString(final String value) {
		this(null, value);
	}

	public JsonString(final JsonPath path, final String value) {
		super(path, value);
	}
}
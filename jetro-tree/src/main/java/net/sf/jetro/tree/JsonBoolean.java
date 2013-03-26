package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public class JsonBoolean extends JsonPrimitive<Boolean> {
	private static final long serialVersionUID = -8707418235663464907L;

	public JsonBoolean() {
		super();
	}

	public JsonBoolean(Boolean value) {
		super(value);
	}

	public JsonBoolean(JsonPath path, Boolean value) {
		super(path, value);
	}

	public JsonBoolean(JsonPath path) {
		super(path);
	}
}
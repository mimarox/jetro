package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public class JsonNumber extends JsonPrimitive<Number> {
	private static final long serialVersionUID = 130455634564941198L;

	public JsonNumber() {
		super();
	}

	public JsonNumber(JsonPath path, Number value) {
		super(path, value);
	}

	public JsonNumber(JsonPath path) {
		super(path);
	}

	public JsonNumber(Number value) {
		super(value);
	}
}
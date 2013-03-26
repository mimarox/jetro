package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public class JsonNull extends JsonPrimitive<Null> {
	private static final long serialVersionUID = 7408732762751496156L;

	public JsonNull() {
		super(Null.instance);
	}

	public JsonNull(JsonPath path) {
		super(path, Null.instance);
	}
}
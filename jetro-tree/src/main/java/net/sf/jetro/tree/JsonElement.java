package net.sf.jetro.tree;

import java.io.Serializable;

public interface JsonElement extends Serializable {

	public String toJson();

	public String toJson(JsonRenderer renderer);
}
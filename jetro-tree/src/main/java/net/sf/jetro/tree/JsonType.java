package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;

public interface JsonType extends JsonElement {
	JsonElement getElementAt(JsonPath path);
}
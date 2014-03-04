package net.sf.jetro.tree;

import net.sf.jetro.path.JsonPath;
import net.sf.jetro.visitor.JsonVisitor;

public interface JsonType extends JsonElement {
	JsonElement getElementAt(JsonPath path);
}
package net.sf.jetro.tree.renderer;


import net.sf.jetro.tree.JsonElement;

public interface JsonRenderer {
	String render(JsonElement element);
}
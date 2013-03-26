package net.sf.jetro.tree;

import java.util.ArrayList;

import net.sf.jetro.tree.renderer.DefaultJsonRenderer;

public class VirtualJsonRoot extends ArrayList<JsonElement> implements JsonElement {
	private static final long serialVersionUID = 463431682418006955L;

	@Override
	public String toJson() {
		return new DefaultJsonRenderer().render(this);
	}

	@Override
	public String toJson(final JsonRenderer renderer) {
		return renderer.render(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VirtualJsonRoot [values=").append(super.toString()).append("]");
		return builder.toString();
	}
}
package net.sf.jetro.tree.renderer;

import net.sf.jetro.context.RenderContext;
import net.sf.jetro.stream.visitor.JsonReturningVisitor;
import net.sf.jetro.tree.JsonElement;
import net.sf.jetro.tree.VirtualJsonRoot;
import net.sf.jetro.tree.visitor.JsonElementVisitingReader;

public class DefaultJsonRenderer implements JsonRenderer {
	private RenderContext context;

	public DefaultJsonRenderer() {
		this(new RenderContext());
	}

	public DefaultJsonRenderer(final RenderContext context) {
		if (context == null) {
			throw new IllegalArgumentException("context must not be null");
		}

		this.context = context;
	}

	@Override
	public String render(final JsonElement element) {
		if (element instanceof VirtualJsonRoot) {
			VirtualJsonRoot rootElement = (VirtualJsonRoot) element;

			StringBuilder renderingResult = new StringBuilder();
			String separator = shouldPrettyPrint() ? ",\n" : ",";

			for (JsonElement childElement : rootElement) {
				renderingResult.append(renderInternal(childElement)).append(separator);
			}

			return renderingResult.substring(0, renderingResult.length() - 1);
		} else {
			return renderInternal(element);
		}
	}

	private String renderInternal(final JsonElement element) {
		JsonElementVisitingReader visitingReader = new JsonElementVisitingReader(element);
		JsonReturningVisitor returningVisitor = new JsonReturningVisitor(context);
		visitingReader.accept(returningVisitor);
		return returningVisitor.getVisitingResult();
	}

	private boolean shouldPrettyPrint() {
		return context.getIndent() != null && !context.getIndent().equals("");
	}
}